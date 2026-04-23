package com.example.campusin.application.rank.scheduler;

import com.example.campusin.application.rank.RankArchiveService;
import com.example.campusin.application.rank.RankWarmUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static com.example.campusin.common.redis.RedisKeyFactory.studyTimeRankKey;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankBatchScheduler {

    private static final int WARM_UP_TOP_PAGE_COUNT = 3;

    private final RankArchiveService rankArchiveService;
    private final RankWarmUpService rankWarmUpService;
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(cron = "0 0 0 * * MON")
    @SchedulerLock(
            name = "archiveLastWeekRanking",
            lockAtMostFor = "PT10M",
            lockAtLeastFor = "PT1M"
    )
    public void archiveLastWeekRanking() {
        LocalDate today = LocalDate.now();
        LocalDate lastWeekStart = today.minusWeeks(1);
        String weekKey = studyTimeRankKey(lastWeekStart);

        if (!archive(weekKey, lastWeekStart)) {
            return;
        }
        warmUp(lastWeekStart);
        cleanUp(weekKey);
    }

    private boolean archive(String weekKey, LocalDate lastWeekStart) {
        try {
            rankArchiveService.archiveRanksInPages(weekKey, lastWeekStart);
            log.info("[RankBatchScheduler] Archive complete. key={}", weekKey);
            return true;
        } catch (Exception e) {
            // 아카이브 실패 시 warm-up/정리로 진행하지 않는다.
            log.error("[RankBatchScheduler] Archive failed. key={}, error={}", weekKey, e.getMessage(), e);
            return false;
        }
    }

    private void warmUp(LocalDate lastWeekStart) {
        try {
            rankWarmUpService.warmUpPopularPages(lastWeekStart, WARM_UP_TOP_PAGE_COUNT);
        } catch (Exception e) {
            // warm-up 실패가 아카이브 결과를 되돌리지 않도록 격리한다.
            log.error("[RankBatchScheduler] Warm-up failed. weekStart={}, error={}",
                    lastWeekStart, e.getMessage(), e);
        }
    }

    private void cleanUp(String weekKey) {
        try {
            redisTemplate.delete(weekKey);
            log.info("[RankBatchScheduler] Redis key deleted. key={}", weekKey);
        } catch (Exception e) {
            log.error("[RankBatchScheduler] Redis key delete failed. key={}, error={}",
                    weekKey, e.getMessage(), e);
        }
    }
}
