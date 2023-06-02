package com.example.campusin.application.timer;

import com.example.campusin.domain.timer.Timer;
import com.example.campusin.domain.timer.request.TimerCreateRequest;
import com.example.campusin.domain.timer.request.TimerUpdateRequest;
import com.example.campusin.domain.timer.response.TimerIdResponse;
import com.example.campusin.domain.timer.response.TimerResponse;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.timer.TimerRepository;
import com.example.campusin.infra.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */
@Service
@AllArgsConstructor
public class TimerService {

    private final TimerRepository timerRepository;
    private final UserRepository userRepository;
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
        return timerRepository.findAll(pageable).map(TimerResponse::new);
    }

    @Transactional
    public TimerIdResponse updateTimer(Long timerId, TimerUpdateRequest timerUpdateRequest) {
        Timer timer = findTimer(timerId);
        timer.updateTimer(timerUpdateRequest);
        return new TimerIdResponse(timerRepository.save(timer).getId());
    }

    @Transactional
    public Page<TimerResponse> initTimer(Long userId, Pageable pageable) {
        findUser(userId);
        List<Timer> oldTimers = timerRepository.findAllByUserId(userId);
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
        findUser(userId);
        findTimer(timerId);
        timerRepository.deleteById(timerId);
    }

    @Transactional
    public LocalDateTime getLastDateTime(Long userId) {
        findUser(userId);
        Timer timer = timerRepository.findTopByUserIdOrderByModifiedAtDesc(userId);
        return timer != null ? timer.getModifiedAt() : null;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
    }

    private Timer findTimer(Long timerId) {
        return timerRepository.findById(timerId)
                .orElseThrow(() -> new IllegalArgumentException("TIMER NOT FOUND"));
    }
}
