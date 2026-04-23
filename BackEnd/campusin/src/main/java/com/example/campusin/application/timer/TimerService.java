package com.example.campusin.application.timer;

import com.example.campusin.application.timer.exception.TimerNotFoundException;
import com.example.campusin.application.timer.exception.TimerUserMismatchException;
import com.example.campusin.application.user.exception.UserNotFoundException;
import com.example.campusin.application.rank.mirror.RankScoreConverter;
import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.timer.Timer;
import com.example.campusin.domain.timer.request.TimerCreateRequest;
import com.example.campusin.domain.timer.request.TimerUpdateRequest;
import com.example.campusin.domain.timer.response.TimerIdResponse;
import com.example.campusin.domain.timer.response.TimerResponse;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.statistics.StatisticsRepository;
import com.example.campusin.infra.timer.TimerRepository;
import com.example.campusin.infra.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.campusin.common.redis.RedisKeyFactory.studyTimeRankKey;
import static com.example.campusin.common.utils.WeekUtil.getWeekStartDate;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */
@Slf4j
@Service
@AllArgsConstructor
public class TimerService {

    private final TimerRepository timerRepository;
    private final UserRepository userRepository;
    private final StatisticsRepository statisticsRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public TimerIdResponse createTimer(Long userId, TimerCreateRequest timerCreateRequest) {
        User user = findUser(userId);
        Timer timer = Timer.builder()
                        .elapsedTime(0L)
                        .subject(timerCreateRequest.getSubject())
                        .user(user)
                        .build();

        return new TimerIdResponse(timerRepository.save(timer).getId());
    }

    @Transactional(readOnly = true)
    public Page<TimerResponse> getAllTimerList(Long userId, Pageable pageable) {
        findUser(userId);
        return timerRepository.findAllMyTimer(userId, pageable).map(TimerResponse::new);
    }

    @Transactional
    public TimerIdResponse updateTimer(Long timerId, TimerUpdateRequest timerUpdateRequest) {
        Timer timer = findTimer(timerId);
        timer.updateTimer(timerUpdateRequest);
        Timer updatedTimer = timerRepository.save(timer);

        Long timeToAdd = timerUpdateRequest.getElapsedTime();
        String userName = timer.getUser().getNickname();
        Long userId = timer.getUser().getId();
        String weekKey = getCurrentWeekRankKey();

        // DB 커밋 성공 이후에만 ZSet을 갱신한다. 커밋 실패 시 Redis에만 반영된 유령 점수를 방지한다.
        registerZSetIncrementAfterCommit(weekKey, userName, timeToAdd, userId);
        return new TimerIdResponse(updatedTimer.getId());
    }

    private void registerZSetIncrementAfterCommit(String weekKey, String userName, long elapsedTimeDelta, Long tieBreakerSource) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    incrementCompositeScore(weekKey, userName, elapsedTimeDelta, tieBreakerSource);
                } catch (Exception e) {
                    // Redis 실패는 DB 커밋을 되돌릴 수 없으므로 로그만 남기고 진행한다.
                    // (추후 과제) 실패 큐로 재시도 보강.
                    log.error("[TimerService] afterCommit ZSet update failed. weekKey={}, user={}, delta={}, error={}",
                            weekKey, userName, elapsedTimeDelta, e.getMessage(), e);
                }
            }
        });
    }

    @Transactional
    public Page<TimerResponse> initTimer(Long userId, Pageable pageable) {
        User user = findUser(userId);
        List<Timer> oldTimers = timerRepository.findAllByUserId(userId);

        Statistics statistics = statisticsRepository.findByUserAndDate(user, oldTimers.get(0).getModifiedAt().toLocalDate().toString());

        if (statistics == null) {
            statistics = Statistics.builder()
                    .date(oldTimers.get(0).getModifiedAt().toLocalDate())
                    .elapsedTime(0L)
                    .build();
        }

        statistics.updateElapsedTime(oldTimers.stream().map(Timer::getElapsedTime).reduce(0L, Long::sum));
        statisticsRepository.save(statistics);

        List<Timer> newTimers = new ArrayList<>();
        for (Timer oldTimer : oldTimers) {
            newTimers.add(Timer.builder()
                    .elapsedTime(0L)
                    .subject(oldTimer.getSubject())
                    .user(oldTimer.getUser())
                    .build());
        }
        timerRepository.deleteAll(oldTimers);
        timerRepository.saveAll(newTimers);
        return getAllTimerList(userId, pageable);
    }
    @Transactional
    public void deleteTimer(Long userId, Long timerId) {
        User user = findUser(userId);
        Timer timer = findTimer(timerId);
        if (!timer.getUser().equals(user)) {
            throw new TimerUserMismatchException();
        }
        Statistics statistics = statisticsRepository.findByUserAndDate(user, timer.getModifiedAt().toLocalDate().toString());
        statistics.addElapsedTime(timer.getElapsedTime());
        statisticsRepository.save(statistics);
        timerRepository.deleteById(timerId);
    }

    @Transactional
    public LocalDateTime getLastDateTime(Long userId) {
        findUser(userId);
        Timer timer = timerRepository.findTopByUserIdOrderByModifiedAtDesc(userId);
        return timer != null ? timer.getModifiedAt() : null;
    }

    private String getCurrentWeekRankKey() {
        LocalDate startOfweek = getWeekStartDate(LocalDate.now());
        return studyTimeRankKey(startOfweek);
    }

    private void incrementCompositeScore(String weekKey, String member, long elapsedTimeDelta, Long tieBreakerSource) {
        Double currentScore = redisTemplate.opsForZSet().score(weekKey, member);
        double deltaScore = RankScoreConverter.toDeltaScore(elapsedTimeDelta);
        if (currentScore == null) {
            double initialScore = RankScoreConverter.initialScore(elapsedTimeDelta, tieBreakerSource);
            redisTemplate.opsForZSet().add(weekKey, member, initialScore);
        } else {
            double baseScore = currentScore;
            // 레거시 점수(plain elapsed time)일 경우 합성 스코어로 변환
            if (currentScore < RankScoreConverter.SCALE) {
                baseScore = RankScoreConverter.fromLegacyTotal(currentScore.longValue(), tieBreakerSource);
                redisTemplate.opsForZSet().add(weekKey, member, baseScore);
            }
            redisTemplate.opsForZSet().incrementScore(weekKey, member, deltaScore);
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private Timer findTimer(Long timerId) {
        return timerRepository.findById(timerId)
                .orElseThrow(TimerNotFoundException::new);
    }
}
