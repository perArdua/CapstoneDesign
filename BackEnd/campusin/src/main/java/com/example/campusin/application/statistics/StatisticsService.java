package com.example.campusin.application.statistics;

import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.statistics.dto.request.StatisticsCreateRequest;
import com.example.campusin.domain.statistics.dto.response.StatisticsIdResponse;
import com.example.campusin.domain.statistics.dto.response.StatisticsResponse;
import com.example.campusin.domain.timer.Timer;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.statistics.StatisticsRepository;
import com.example.campusin.infra.timer.TimerRepository;
import com.example.campusin.infra.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by kok8454@gmail.com on 2023-06-02
 * Github : http://github.com/perArdua
 */
@Service
@AllArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final UserRepository userRepository;
    private final TimerRepository timerRepository;
    @Transactional
    public StatisticsIdResponse createStatistics(Long userId, StatisticsCreateRequest statisticsCreateRequest) {
        User user = findUser(userId);

        Statistics oldStatistics = statisticsRepository.findByUserAndDate(user, statisticsCreateRequest.getLocalDate());
        if (oldStatistics != null) {
            return new StatisticsIdResponse(oldStatistics.getId());
        }

        Statistics statistics = Statistics.builder()
                .user(user)
                .date(statisticsCreateRequest.getLocalDate())
                .elapsedTime(0L)
                .numberOfAnswers(0L)
                .numberOfQuestions(0L)
                .numberOfAdoptedAnswers(0L)
                .build();

        return new StatisticsIdResponse(statisticsRepository.save(statistics).getId());
    }

    @Transactional
    public StatisticsResponse readStatistics(Long userId, Long statisticsId) {
        User user = findUser(userId);
        Statistics statistics = findStatistics(statisticsId);
        LocalDate localDate = statistics.getDate();

        List<Timer> timerList = timerRepository.findAllByUserAndModifiedAtBetween(user, localDate, localDate.plusDays(1));

        Long totalElapsedTime = timerList.stream().mapToLong(Timer::getElapsedTime).sum();
        Long numberOfQuestions = statisticsRepository.countQuestionsByUserAndModifiedAtBetween(user, localDate, localDate.plusDays(1));
        Long numberOfAnswers = statisticsRepository.countAnswersByUserAndModifiedAtBetweenAndIsAnswerTrue(user, localDate, localDate.plusDays(1));
        Long numberOfAdoptedAnswers = statisticsRepository.countAnswersByUserAndModifiedAtBetweenAndIsAdoptedTrue(user, localDate, localDate.plusDays(1));

        if (localDate.isEqual(LocalDate.now())) {
            statistics.updateElapsedTime(totalElapsedTime);
        }
        statistics.updateNumberOfQuestions(numberOfQuestions);
        statistics.updateNumberOfAnswers(numberOfAnswers);
        statistics.updateNumberOfAdoptedAnswers(numberOfAdoptedAnswers);
        statisticsRepository.save(statistics);
        return new StatisticsResponse(statistics.getElapsedTime(), statistics.getNumberOfQuestions(), statistics.getNumberOfAnswers(), statistics.getNumberOfAdoptedAnswers());
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
    }

    private Statistics findStatistics(Long statisticsId) {
        return statisticsRepository.findById(statisticsId)
                .orElseThrow(() -> new IllegalArgumentException("STATISTICS NOT FOUND"));
    }
}
