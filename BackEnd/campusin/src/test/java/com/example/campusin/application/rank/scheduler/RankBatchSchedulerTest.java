package com.example.campusin.application.rank.scheduler;

import com.example.campusin.application.rank.RankArchiveService;
import com.example.campusin.application.rank.RankWarmUpService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;

import static com.example.campusin.common.redis.RedisKeyFactory.studyTimeRankKey;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RankBatchScheduler")
class RankBatchSchedulerTest {

    @Mock
    RankArchiveService rankArchiveService;
    @Mock
    RankWarmUpService rankWarmUpService;
    @Mock
    RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    RankBatchScheduler rankBatchScheduler;

    @Nested
    @DisplayName("archiveLastWeekRanking 메서드는")
    class Describe_archiveLastWeekRanking {

        @Test
        @DisplayName("아카이브 → warm-up → Redis 키 삭제 순서로 실행한다")
        void 순서대로_실행한다() {
            // given
            LocalDate lastWeekStart = LocalDate.now().minusWeeks(1);
            String expectedKey = studyTimeRankKey(lastWeekStart);

            // when
            rankBatchScheduler.archiveLastWeekRanking();

            // then
            InOrder order = inOrder(rankArchiveService, rankWarmUpService, redisTemplate);
            order.verify(rankArchiveService).archiveRanksInPages(eq(expectedKey), eq(lastWeekStart));
            order.verify(rankWarmUpService).warmUpPopularPages(eq(lastWeekStart), anyInt());
            order.verify(redisTemplate).delete(expectedKey);
        }

        @Test
        @DisplayName("아카이브가 실패하면 warm-up과 Redis 삭제를 시도하지 않는다")
        void 아카이브_실패시_이후단계_생략() {
            // given
            LocalDate lastWeekStart = LocalDate.now().minusWeeks(1);
            String expectedKey = studyTimeRankKey(lastWeekStart);
            willThrow(new RuntimeException("archive fail"))
                    .given(rankArchiveService).archiveRanksInPages(expectedKey, lastWeekStart);

            // when
            rankBatchScheduler.archiveLastWeekRanking();

            // then
            verify(rankWarmUpService, never()).warmUpPopularPages(any(LocalDate.class), anyInt());
            verify(redisTemplate, never()).delete(expectedKey);
        }

        @Test
        @DisplayName("warm-up이 실패해도 Redis 키 삭제는 계속 진행한다")
        void warmup_실패시_정리는_계속() {
            // given
            LocalDate lastWeekStart = LocalDate.now().minusWeeks(1);
            String expectedKey = studyTimeRankKey(lastWeekStart);
            willThrow(new RuntimeException("warm-up fail"))
                    .given(rankWarmUpService).warmUpPopularPages(eq(lastWeekStart), anyInt());

            // when
            rankBatchScheduler.archiveLastWeekRanking();

            // then
            verify(rankArchiveService).archiveRanksInPages(expectedKey, lastWeekStart);
            verify(redisTemplate).delete(expectedKey);
        }
    }
}
