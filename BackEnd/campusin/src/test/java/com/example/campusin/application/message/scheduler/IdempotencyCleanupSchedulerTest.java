package com.example.campusin.application.message.scheduler;

import com.example.campusin.infra.message.MessageRoomIdempotencyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("IdempotencyCleanupScheduler")
class IdempotencyCleanupSchedulerTest {

    @Mock
    MessageRoomIdempotencyRepository messageRoomIdempotencyRepository;

    @InjectMocks
    IdempotencyCleanupScheduler scheduler;

    @Nested
    @DisplayName("cleanupExpired 메서드는")
    class Describe_cleanupExpired {

        @Test
        @DisplayName("24시간 이전 cutoff와 배치 사이즈 1000으로 저장소를 호출한다")
        void cutoff는_24시간_전이고_배치사이즈는_1000이다() {
            // given
            LocalDateTime before = LocalDateTime.now().minusHours(24);
            when(messageRoomIdempotencyRepository.deleteExpired(any(LocalDateTime.class), eq(1000)))
                    .thenReturn(0);

            // when
            scheduler.cleanupExpired();

            // then
            ArgumentCaptor<LocalDateTime> cutoffCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
            verify(messageRoomIdempotencyRepository).deleteExpired(cutoffCaptor.capture(), eq(1000));

            LocalDateTime cutoff = cutoffCaptor.getValue();
            long diffSeconds = Math.abs(ChronoUnit.SECONDS.between(cutoff, before));
            assertThat(diffSeconds).isLessThan(5);
        }

        @Test
        @DisplayName("삭제가 0이 될 때까지 루프를 돈다")
        void 루프를_돈다() {
            // given — 2000건을 두 번의 배치로 삭제 후 종료
            when(messageRoomIdempotencyRepository.deleteExpired(any(LocalDateTime.class), eq(1000)))
                    .thenReturn(1000)
                    .thenReturn(1000)
                    .thenReturn(0);

            // when
            scheduler.cleanupExpired();

            // then
            verify(messageRoomIdempotencyRepository, times(3))
                    .deleteExpired(any(LocalDateTime.class), eq(1000));
        }

        @Test
        @DisplayName("첫 호출부터 0이면 한 번만 호출한다")
        void 삭제할_것이_없으면_한_번만_호출한다() {
            // given
            when(messageRoomIdempotencyRepository.deleteExpired(any(LocalDateTime.class), eq(1000)))
                    .thenReturn(0);

            // when
            scheduler.cleanupExpired();

            // then
            verify(messageRoomIdempotencyRepository, times(1))
                    .deleteExpired(any(LocalDateTime.class), eq(1000));
        }
    }
}
