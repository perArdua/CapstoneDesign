package com.example.campusin.application.statistics;

import com.example.campusin.application.statistics.exception.StatisticsNotFoundException;
import com.example.campusin.application.user.exception.UserNotFoundException;
import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.statistics.dto.request.StatisticsCreateRequest;
import com.example.campusin.domain.statistics.dto.response.StatisticsIdResponse;
import com.example.campusin.domain.statistics.dto.response.StatisticsResponse;
import com.example.campusin.domain.timer.Timer;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.statistics.StatisticsRepository;
import com.example.campusin.infra.timer.TimerRepository;
import com.example.campusin.infra.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StatisticsService")
class StatisticsServiceTest {

    @Mock
    StatisticsRepository statisticsRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    TimerRepository timerRepository;

    @InjectMocks
    StatisticsService statisticsService;

    private User newUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setLoginId("login-" + id);
        return user;
    }

    private Statistics newStatistics(User user, LocalDate date) {
        Statistics statistics = Statistics.builder()
                .user(user)
                .date(date)
                .elapsedTime(0L)
                .numberOfAnswers(0L)
                .numberOfQuestions(0L)
                .numberOfAdoptedAnswers(0L)
                .build();
        ReflectionTestUtils.setField(statistics, "id", 10L);
        return statistics;
    }

    @Nested
    @DisplayName("createStatistics 메서드는")
    class Describe_createStatistics {

        @Test
        @DisplayName("사용자가 없으면 UserNotFoundException을 던진다")
        void 사용자없음() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> statisticsService.createStatistics(1L, new StatisticsCreateRequest(LocalDate.now())))
                    .isInstanceOf(UserNotFoundException.class);

            verifyNoInteractions(statisticsRepository);
        }

        @Test
        @DisplayName("해당 날짜 통계가 이미 있으면 기존 ID를 반환한다")
        void 기존통계반환() {
            // given
            User user = newUser(1L);
            Statistics existing = newStatistics(user, LocalDate.now());
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(statisticsRepository.findByUserAndDate(user, LocalDate.now().toString())).thenReturn(existing);

            // when
            StatisticsIdResponse response = statisticsService.createStatistics(1L, new StatisticsCreateRequest(LocalDate.now()));

            // then
            assertThat(response.getId()).isEqualTo(existing.getId());
            verify(statisticsRepository, never()).save(any(Statistics.class));
        }

        @Test
        @DisplayName("새 통계를 저장하고 ID를 반환한다")
        void 새통계저장() {
            // given
            User user = newUser(1L);
            Statistics saved = newStatistics(user, LocalDate.now());
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(statisticsRepository.findByUserAndDate(user, LocalDate.now().toString())).thenReturn(null);
            when(statisticsRepository.save(any(Statistics.class))).thenReturn(saved);

            // when
            StatisticsIdResponse response = statisticsService.createStatistics(1L, new StatisticsCreateRequest(LocalDate.now()));

            // then
            assertThat(response.getId()).isEqualTo(saved.getId());
        }
    }

    @Nested
    @DisplayName("readStatistics 메서드는")
    class Describe_readStatistics {

        @Test
        @DisplayName("사용자가 없으면 UserNotFoundException을 던진다")
        void 사용자없음() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> statisticsService.readStatistics(1L, 2L))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("통계가 없으면 StatisticsNotFoundException을 던진다")
        void 통계없음() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(newUser(1L)));
            when(statisticsRepository.findById(2L)).thenReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> statisticsService.readStatistics(1L, 2L))
                    .isInstanceOf(StatisticsNotFoundException.class);
        }

        @Test
        @DisplayName("오늘이면 타이머 합산 값을 반영해 통계를 업데이트한다")
        void 오늘이면합산() {
            // given
            User user = newUser(1L);
            LocalDate today = LocalDate.now();
            Statistics statistics = newStatistics(user, today);
            Timer timer = Timer.builder().user(user).elapsedTime(5L).build();
            ReflectionTestUtils.setField(timer, "modifiedAt", LocalDateTime.now());

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(statisticsRepository.findById(2L)).thenReturn(Optional.of(statistics));
            when(timerRepository.findAllByUserAndModifiedAtBetween(eq(1L), anyString(), anyString()))
                    .thenReturn(List.of(timer));
            when(statisticsRepository.countQuestionsByUserAndModifiedAtBetween(eq(user), anyString(), anyString()))
                    .thenReturn(1L);
            when(statisticsRepository.countAnswersByUserAndModifiedAtBetweenAndIsAnswerTrue(eq(user), anyString(), anyString()))
                    .thenReturn(2L);
            when(statisticsRepository.countAnswersByUserAndModifiedAtBetweenAndIsAdoptedTrue(eq(user), anyString(), anyString()))
                    .thenReturn(3L);
            when(statisticsRepository.save(any(Statistics.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            StatisticsResponse response = statisticsService.readStatistics(1L, 2L);

            // then
            assertThat(response.getTotalElapsedTime()).isEqualTo(5L);
            assertThat(response.getNumberOfQuestions()).isEqualTo(1L);
            assertThat(response.getNumberOfAnswers()).isEqualTo(2L);
            assertThat(response.getNumberOfAdoptedAnswers()).isEqualTo(3L);
            verify(statisticsRepository, times(1)).save(statistics);
        }
    }
}
