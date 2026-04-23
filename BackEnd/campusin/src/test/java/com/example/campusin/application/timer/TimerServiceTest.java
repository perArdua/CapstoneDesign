package com.example.campusin.application.timer;

import com.example.campusin.application.timer.exception.TimerNotFoundException;
import com.example.campusin.application.timer.exception.TimerUserMismatchException;
import com.example.campusin.application.user.exception.UserNotFoundException;
import com.example.campusin.common.redis.RedisKeyFactory;
import com.example.campusin.common.utils.WeekUtil;
import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.timer.Timer;
import com.example.campusin.domain.timer.request.TimerCreateRequest;
import com.example.campusin.domain.timer.request.TimerUpdateRequest;
import com.example.campusin.domain.timer.response.TimerIdResponse;
import com.example.campusin.domain.timer.response.TimerResponse;
import com.example.campusin.domain.user.User;
import com.example.campusin.application.rank.mirror.RankScoreConverter;
import com.example.campusin.infra.statistics.StatisticsRepository;
import com.example.campusin.infra.timer.TimerRepository;
import com.example.campusin.infra.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.example.campusin.application.rank.mirror.RankScoreConverter.toDeltaScore;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimerService")
class TimerServiceTest {

    @Mock
    TimerRepository timerRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    StatisticsRepository statisticsRepository;
    @Mock
    RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    TimerService timerService;

    @Nested
    @DisplayName("createTimer 메서드는")
    class Describe_createTimer {

        @Nested
        @DisplayName("사용자가 존재하면")
        class Context_when_user_exists {

            @Test
            @DisplayName("elapsedTime 0과 요청한 subject로 타이머를 생성하고 ID를 반환한다")
            void 타이머를_생성한다() {
                // given
                Long userId = 1L;
                String subject = "자료구조";
                User user = new User();
                user.setId(userId);
                TimerCreateRequest request = new TimerCreateRequest(subject);

                ArgumentCaptor<Timer> timerCaptor = ArgumentCaptor.forClass(Timer.class);

                given(userRepository.findById(userId)).willReturn(Optional.of(user));
                given(timerRepository.save(timerCaptor.capture()))
                        .willReturn(Timer.builder().id(10L).subject(subject).elapsedTime(0L).user(user).build());

                // when
                TimerIdResponse response = timerService.createTimer(userId, request);

                // then
                Timer savedTimer = timerCaptor.getValue();
                assertThat(response.getId()).isEqualTo(10L);
                assertThat(savedTimer.getElapsedTime()).isZero();
                assertThat(savedTimer.getSubject()).isEqualTo(subject);
                assertThat(savedTimer.getUser()).isEqualTo(user);
                verifyNoInteractions(statisticsRepository, redisTemplate);
            }
        }

        @Nested
        @DisplayName("사용자가 존재하지 않으면")
        class Context_when_user_is_missing {

            @Test
            @DisplayName("UserNotFoundException을 던지고 리포지토리들을 호출하지 않는다")
            void 예외를_던진다() {
                // given
                Long userId = 9L;
                TimerCreateRequest request = new TimerCreateRequest("OS");

                given(userRepository.findById(userId)).willReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> timerService.createTimer(userId, request))
                        .isInstanceOf(UserNotFoundException.class);

                verifyNoInteractions(timerRepository, statisticsRepository, redisTemplate);
            }
        }
    }

    @Nested
    @DisplayName("getAllTimerList 메서드는")
    class Describe_getAllTimerList {

        @Nested
        @DisplayName("사용자가 존재하면")
        class Context_when_user_exists {

            @Test
            @DisplayName("사용자 ID에 해당하는 타이머 목록을 TimerResponse로 반환한다")
            void 타이머_목록을_반환한다() {
                // given
                Long userId = 2L;
                User user = new User();
                user.setId(userId);
                Timer timer = Timer.builder()
                        .id(1L)
                        .subject("알고리즘")
                        .elapsedTime(120L)
                        .user(user)
                        .build();
                PageRequest pageable = PageRequest.of(0, 10);

                given(userRepository.findById(userId)).willReturn(Optional.of(user));
                given(timerRepository.findAllMyTimer(userId, pageable))
                        .willReturn(new PageImpl<>(List.of(timer), pageable, 1));

                // when
                Page<TimerResponse> responses = timerService.getAllTimerList(userId, pageable);

                // then
                assertThat(responses.getTotalElements()).isEqualTo(1);
                TimerResponse response = responses.getContent().get(0);
                assertThat(response.getId()).isEqualTo(timer.getId());
                assertThat(response.getSubject()).isEqualTo(timer.getSubject());
                assertThat(response.getElapsedTime()).isEqualTo(timer.getElapsedTime());
                assertThat(response.getUserId()).isEqualTo(userId);
                verifyNoInteractions(statisticsRepository, redisTemplate);
            }
        }

        @Nested
        @DisplayName("사용자가 존재하지 않으면")
        class Context_when_user_is_missing {

            @Test
            @DisplayName("UserNotFoundException을 던진다")
            void 예외를_던진다() {
                // given
                Long userId = 99L;
                PageRequest pageable = PageRequest.of(0, 5);
                given(userRepository.findById(userId)).willReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> timerService.getAllTimerList(userId, pageable))
                        .isInstanceOf(UserNotFoundException.class);

                verifyNoInteractions(timerRepository, statisticsRepository, redisTemplate);
            }
        }
    }

    @Nested
    @DisplayName("updateTimer 메서드는")
    class Describe_updateTimer {

        @Nested
        @DisplayName("타이머 시간이 증가하는 요청이 들어오면")
        class Context_when_time_increases {

            @Test
            @DisplayName("DB 커밋 전에는 ZSet을 갱신하지 않고 afterCommit 이후에만 갱신한다")
            void 커밋_이후에만_랭킹에_반영한다() {
                // given
                Long timerId = 5L;
                User owner = new User();
                owner.setId(1L);
                owner.setNickname("john");
                Timer existingTimer = Timer.builder()
                        .id(timerId)
                        .user(owner)
                        .elapsedTime(10L)
                        .build();

                TimerUpdateRequest request = new TimerUpdateRequest(5L);
                ZSetOperations<String, String> zSetOperations = mock(ZSetOperations.class);

                given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
                given(timerRepository.findById(timerId)).willReturn(Optional.of(existingTimer));
                given(timerRepository.save(any(Timer.class))).willAnswer(invocation -> invocation.getArgument(0));

                String expectedWeekKey = RedisKeyFactory.studyTimeRankKey(WeekUtil.getWeekStartDate(LocalDate.now()));

                TransactionSynchronizationManager.initSynchronization();
                try {
                    // when
                    TimerIdResponse response = timerService.updateTimer(timerId, request);

                    // then — 커밋 이전에는 ZSet 호출이 없다
                    assertThat(response.getId()).isEqualTo(timerId);
                    assertThat(existingTimer.getElapsedTime()).isEqualTo(15L);
                    verify(zSetOperations, never()).incrementScore(any(), any(), anyDouble());

                    // when — afterCommit 트리거
                    TransactionSynchronizationUtils.triggerAfterCommit();

                    // then — 커밋 이후에 정확한 키/멤버/점수로 ZSet이 갱신된다
                    ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
                    ArgumentCaptor<String> memberCaptor = ArgumentCaptor.forClass(String.class);
                    ArgumentCaptor<Double> scoreCaptor = ArgumentCaptor.forClass(Double.class);
                    verify(zSetOperations).incrementScore(keyCaptor.capture(), memberCaptor.capture(), scoreCaptor.capture());
                    assertThat(keyCaptor.getValue()).isEqualTo(expectedWeekKey);
                    assertThat(memberCaptor.getValue()).isEqualTo(owner.getNickname());
                    assertThat(scoreCaptor.getValue()).isEqualTo(toDeltaScore(request.getElapsedTime()));
                } finally {
                    TransactionSynchronizationManager.clearSynchronization();
                }
            }

            @Test
            @DisplayName("경계값 0이라도 저장하고 커밋 이후에 랭킹에 0을 반영한다")
            void 경계값_0을_반영한다() {
                // given
                Long timerId = 8L;
                User owner = new User();
                owner.setId(1L);
                owner.setNickname("amy");
                Timer existingTimer = Timer.builder()
                        .id(timerId)
                        .user(owner)
                        .elapsedTime(20L)
                        .build();

                TimerUpdateRequest request = new TimerUpdateRequest(0L);
                ZSetOperations<String, String> zSetOperations = mock(ZSetOperations.class);

                given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
                given(timerRepository.findById(timerId)).willReturn(Optional.of(existingTimer));
                given(timerRepository.save(any(Timer.class))).willAnswer(invocation -> invocation.getArgument(0));

                TransactionSynchronizationManager.initSynchronization();
                try {
                    // when
                    TimerIdResponse response = timerService.updateTimer(timerId, request);
                    TransactionSynchronizationUtils.triggerAfterCommit();

                    // then
                    assertThat(response.getId()).isEqualTo(timerId);
                    assertThat(existingTimer.getElapsedTime()).isEqualTo(20L);
                    verify(zSetOperations).incrementScore(any(), eq(owner.getNickname()), eq(0.0));
                } finally {
                    TransactionSynchronizationManager.clearSynchronization();
                }
            }

            @Test
            @DisplayName("트랜잭션이 롤백되면 ZSet을 갱신하지 않는다")
            void 롤백시_ZSet_미갱신() {
                // given
                Long timerId = 6L;
                User owner = new User();
                owner.setId(2L);
                owner.setNickname("doe");
                Timer existingTimer = Timer.builder()
                        .id(timerId)
                        .user(owner)
                        .elapsedTime(5L)
                        .build();

                TimerUpdateRequest request = new TimerUpdateRequest(7L);

                given(timerRepository.findById(timerId)).willReturn(Optional.of(existingTimer));
                given(timerRepository.save(any(Timer.class))).willAnswer(invocation -> invocation.getArgument(0));

                TransactionSynchronizationManager.initSynchronization();
                try {
                    // when — afterCommit을 트리거하지 않고 종료 (롤백 상황 시뮬레이션)
                    timerService.updateTimer(timerId, request);

                    // then
                    verifyNoInteractions(redisTemplate);
                } finally {
                    TransactionSynchronizationManager.clearSynchronization();
                }
            }
        }

        @Nested
        @DisplayName("타이머가 존재하지 않으면")
        class Context_when_timer_missing {

            @Test
            @DisplayName("TimerNotFoundException을 던지고 Redis를 호출하지 않는다")
            void 예외를_던진다() {
                // given
                Long timerId = 55L;
                given(timerRepository.findById(timerId)).willReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> timerService.updateTimer(timerId, new TimerUpdateRequest(3L)))
                        .isInstanceOf(TimerNotFoundException.class);

                verifyNoInteractions(redisTemplate, statisticsRepository);
            }
        }
    }

    @Nested
    @DisplayName("initTimer 메서드는")
    class Describe_initTimer {

        @Nested
        @DisplayName("기존 통계가 존재하면")
        class Context_when_statistics_exist {

            @Test
            @DisplayName("기존 통계를 당일 합계로 덮어쓰고 타이머를 초기화한다")
            void 통계를_덮어쓰고_타이머를_초기화한다() {
                // given
                Long userId = 3L;
                User user = new User();
                user.setId(userId);

                Timer oldTimer1 = Timer.builder().id(1L).subject("자료구조").elapsedTime(3L).user(user).build();
                Timer oldTimer2 = Timer.builder().id(2L).subject("운영체제").elapsedTime(7L).user(user).build();
                LocalDateTime modifiedAt = LocalDateTime.of(2023, 1, 2, 10, 0);
                ReflectionTestUtils.setField(oldTimer1, "modifiedAt", modifiedAt);
                ReflectionTestUtils.setField(oldTimer2, "modifiedAt", modifiedAt.plusHours(1));

                Statistics statistics = Statistics.builder()
                        .id(10L)
                        .elapsedTime(5L)
                        .date(modifiedAt.toLocalDate())
                        .build();

                PageRequest pageable = PageRequest.of(0, 10);
                AtomicReference<List<Timer>> savedTimers = new AtomicReference<>();

                given(userRepository.findById(userId)).willReturn(Optional.of(user));
                given(timerRepository.findAllByUserId(userId)).willReturn(List.of(oldTimer1, oldTimer2));
                given(statisticsRepository.findByUserAndDate(user, modifiedAt.toLocalDate().toString()))
                        .willReturn(statistics);
                given(timerRepository.saveAll(any())).willAnswer(invocation -> {
                    List<Timer> timers = invocation.getArgument(0);
                    savedTimers.set(timers);
                    return timers;
                });
                given(timerRepository.findAllMyTimer(eq(userId), eq(pageable)))
                        .willAnswer(invocation -> new PageImpl<>(savedTimers.get(), pageable, savedTimers.get().size()));

                // when
                Page<TimerResponse> responses = timerService.initTimer(userId, pageable);

                // then
                assertThat(statistics.getElapsedTime()).isEqualTo(10L);
                assertThat(savedTimers.get()).hasSize(2)
                        .allSatisfy(timer -> {
                            assertThat(timer.getElapsedTime()).isZero();
                            assertThat(timer.getUser()).isEqualTo(user);
                        });
                assertThat(responses.getTotalElements()).isEqualTo(2);
                verify(timerRepository).deleteAll(List.of(oldTimer1, oldTimer2));
                verify(statisticsRepository).save(statistics);
            }
        }

        @Nested
        @DisplayName("기존 통계가 없으면")
        class Context_when_statistics_absent {

            @Test
            @DisplayName("새 통계를 생성해 저장하고 타이머를 초기화한다")
            void 새_통계를_생성한다() {
                // given
                Long userId = 4L;
                User user = new User();
                user.setId(userId);

                Timer oldTimer = Timer.builder().id(11L).subject("네트워크").elapsedTime(4L).user(user).build();
                LocalDateTime modifiedAt = LocalDateTime.of(2023, 2, 10, 9, 0);
                ReflectionTestUtils.setField(oldTimer, "modifiedAt", modifiedAt);

                PageRequest pageable = PageRequest.of(0, 5);
                ArgumentCaptor<Statistics> statisticsCaptor = ArgumentCaptor.forClass(Statistics.class);
                AtomicReference<List<Timer>> savedTimers = new AtomicReference<>();

                given(userRepository.findById(userId)).willReturn(Optional.of(user));
                given(timerRepository.findAllByUserId(userId)).willReturn(List.of(oldTimer));
                given(statisticsRepository.findByUserAndDate(user, modifiedAt.toLocalDate().toString()))
                        .willReturn(null);
                given(timerRepository.saveAll(any())).willAnswer(invocation -> {
                    List<Timer> timers = invocation.getArgument(0);
                    savedTimers.set(timers);
                    return timers;
                });
                given(timerRepository.findAllMyTimer(eq(userId), eq(pageable)))
                        .willAnswer(invocation -> new PageImpl<>(savedTimers.get(), pageable, savedTimers.get().size()));

                // when
                Page<TimerResponse> responses = timerService.initTimer(userId, pageable);

                // then
                verify(statisticsRepository).save(statisticsCaptor.capture());
                Statistics savedStatistics = statisticsCaptor.getValue();
                assertThat(savedStatistics.getElapsedTime()).isEqualTo(4L);
                assertThat(savedStatistics.getDate()).isEqualTo(modifiedAt.toLocalDate());

                assertThat(savedTimers.get()).singleElement().satisfies(timer -> {
                    assertThat(timer.getSubject()).isEqualTo("네트워크");
                    assertThat(timer.getElapsedTime()).isZero();
                });
                assertThat(responses.getTotalElements()).isEqualTo(1);
                verify(timerRepository).deleteAll(List.of(oldTimer));
            }
        }

        @Nested
        @DisplayName("사용자가 존재하지 않으면")
        class Context_when_user_missing {

            @Test
            @DisplayName("UserNotFoundException을 던진다")
            void 예외를_던진다() {
                // given
                Long userId = 7L;
                PageRequest pageable = PageRequest.of(0, 1);
                given(userRepository.findById(userId)).willReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> timerService.initTimer(userId, pageable))
                        .isInstanceOf(UserNotFoundException.class);

                verifyNoInteractions(timerRepository, statisticsRepository, redisTemplate);
            }
        }
    }

    @Nested
    @DisplayName("deleteTimer 메서드는")
    class Describe_deleteTimer {

        @Nested
        @DisplayName("타이머 소유자가 아닌 사용자가 삭제를 요청하면")
        class Context_when_user_is_not_owner {

            @Test
            @DisplayName("TimerUserMismatchException을 던지고 통계/Redis는 호출하지 않는다")
            void 예외를_던진다() {
                // given
                Long userId = 1L;
                Long timerId = 10L;
                User timerOwner = new User();
                User otherUser = new User();

                given(userRepository.findById(userId)).willReturn(Optional.of(otherUser));
                given(timerRepository.findById(timerId))
                        .willReturn(Optional.of(Timer.builder().id(timerId).user(timerOwner).build()));

                // when // then
                assertThatThrownBy(() -> timerService.deleteTimer(userId, timerId))
                        .isInstanceOf(TimerUserMismatchException.class);

                verifyNoInteractions(statisticsRepository);
                verifyNoInteractions(redisTemplate);
            }
        }

        @Nested
        @DisplayName("타이머 소유자가 삭제를 요청하면")
        class Context_when_owner_deletes {

            @Test
            @DisplayName("통계에 시간을 누적해 저장하고 타이머를 삭제한다")
            void 통계를_누적하고_삭제한다() {
                // given
                Long userId = 2L;
                Long timerId = 20L;
                User owner = new User();
                owner.setId(userId);
                Timer timer = Timer.builder().id(timerId).user(owner).elapsedTime(3L).build();
                LocalDateTime modifiedAt = LocalDateTime.of(2023, 3, 5, 8, 0);
                ReflectionTestUtils.setField(timer, "modifiedAt", modifiedAt);

                Statistics statistics = Statistics.builder()
                        .id(30L)
                        .elapsedTime(5L)
                        .date(modifiedAt.toLocalDate())
                        .user(owner)
                        .build();

                given(userRepository.findById(userId)).willReturn(Optional.of(owner));
                given(timerRepository.findById(timerId)).willReturn(Optional.of(timer));
                given(statisticsRepository.findByUserAndDate(owner, modifiedAt.toLocalDate().toString()))
                        .willReturn(statistics);

                // when
                timerService.deleteTimer(userId, timerId);

                // then
                assertThat(statistics.getElapsedTime()).isEqualTo(8L);
                verify(statisticsRepository).save(statistics);
                verify(timerRepository).deleteById(timerId);
                verifyNoInteractions(redisTemplate);
            }
        }

        @Nested
        @DisplayName("타이머가 없으면")
        class Context_when_timer_missing {

            @Test
            @DisplayName("TimerNotFoundException을 던진다")
            void 예외를_던진다() {
                // given
                Long userId = 12L;
                Long timerId = 100L;
                given(userRepository.findById(userId)).willReturn(Optional.of(new User()));
                given(timerRepository.findById(timerId)).willReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> timerService.deleteTimer(userId, timerId))
                        .isInstanceOf(TimerNotFoundException.class);

                verifyNoInteractions(statisticsRepository, redisTemplate);
            }
        }

        @Nested
        @DisplayName("사용자가 없으면")
        class Context_when_user_missing {

            @Test
            @DisplayName("UserNotFoundException을 던진다")
            void 예외를_던진다() {
                // given
                Long userId = 15L;
                Long timerId = 200L;
                given(userRepository.findById(userId)).willReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> timerService.deleteTimer(userId, timerId))
                        .isInstanceOf(UserNotFoundException.class);

                verifyNoInteractions(timerRepository, statisticsRepository, redisTemplate);
            }
        }
    }

    @Nested
    @DisplayName("getLastDateTime 메서드는")
    class Describe_getLastDateTime {

        @Nested
        @DisplayName("가장 최근 타이머가 존재하면")
        class Context_when_timer_exists {

            @Test
            @DisplayName("최근 수정 시각을 반환한다")
            void 최근_수정시각을_반환한다() {
                // given
                Long userId = 22L;
                LocalDateTime modifiedAt = LocalDateTime.of(2023, 6, 1, 12, 30);
                Timer latestTimer = Timer.builder().id(1L).elapsedTime(10L).build();
                ReflectionTestUtils.setField(latestTimer, "modifiedAt", modifiedAt);

                given(userRepository.findById(userId)).willReturn(Optional.of(new User()));
                given(timerRepository.findTopByUserIdOrderByModifiedAtDesc(userId)).willReturn(latestTimer);

                // when
                LocalDateTime result = timerService.getLastDateTime(userId);

                // then
                assertThat(result).isEqualTo(modifiedAt);
                verifyNoInteractions(statisticsRepository, redisTemplate);
            }
        }

        @Nested
        @DisplayName("타이머가 없으면")
        class Context_when_timer_missing {

            @Test
            @DisplayName("null을 반환한다")
            void null을_반환한다() {
                // given
                Long userId = 30L;
                given(userRepository.findById(userId)).willReturn(Optional.of(new User()));
                given(timerRepository.findTopByUserIdOrderByModifiedAtDesc(userId)).willReturn(null);

                // when
                LocalDateTime result = timerService.getLastDateTime(userId);

                // then
                assertThat(result).isNull();
            }
        }

        @Nested
        @DisplayName("사용자가 없으면")
        class Context_when_user_missing {

            @Test
            @DisplayName("UserNotFoundException을 던진다")
            void 예외를_던진다() {
                // given
                Long userId = 31L;
                given(userRepository.findById(userId)).willReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> timerService.getLastDateTime(userId))
                        .isInstanceOf(UserNotFoundException.class);

                verifyNoInteractions(timerRepository, statisticsRepository, redisTemplate);
            }
        }
    }
}
