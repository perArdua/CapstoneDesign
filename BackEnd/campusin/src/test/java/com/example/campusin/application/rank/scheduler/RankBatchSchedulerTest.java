package com.example.campusin.application.rank.scheduler;

import com.example.campusin.application.rank.RankArchiveService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;

import static com.example.campusin.common.redis.RedisKeyFactory.studyTimeRankKey;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RankBatchScheduler")
class RankBatchSchedulerTest {

    @Mock
    RankArchiveService rankArchiveService;
    @Mock
    RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    RankBatchScheduler rankBatchScheduler;

    @Nested
    @DisplayName("archiveLastWeekRanking 메서드는")
    class Describe_archiveLastWeekRanking {

        @Test
        @DisplayName("지난주 키를 계산해 아카이브하고 Redis 키를 삭제한다")
        void 지난주_키를_삭제한다() {
            // given
            LocalDate lastWeekStart = LocalDate.now().minusWeeks(1);
            String expectedKey = studyTimeRankKey(lastWeekStart);

            // when
            rankBatchScheduler.archiveLastWeekRanking();

            // then
            verify(rankArchiveService).archiveRanksInPages(eq(expectedKey), eq(lastWeekStart));
            verify(redisTemplate).delete(expectedKey);
        }

        @Test
        @DisplayName("아카이브 중 예외가 발생하면 키 삭제를 시도하지 않는다")
        void 예외시_삭제하지_않는다() {
            // given
            LocalDate lastWeekStart = LocalDate.now().minusWeeks(1);
            String expectedKey = studyTimeRankKey(lastWeekStart);
            willThrow(new RuntimeException("fail"))
                    .given(rankArchiveService).archiveRanksInPages(expectedKey, lastWeekStart);

            // when
            rankBatchScheduler.archiveLastWeekRanking();

            // then
            verify(redisTemplate, never()).delete(expectedKey);
        }
    }
}
