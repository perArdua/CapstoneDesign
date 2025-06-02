package com.example.campusin.application.rank.scheduler;

import com.example.campusin.application.rank.RankArchiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static com.example.campusin.common.redis.RedisKeyFactory.studyTimeRankKey;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankBatchScheduler {

    private final RankArchiveService rankArchiveService;
    private final RedisTemplate<String, String> redisTemplate;

@Scheduled(cron = "0 0 0 * * MON")
public void archiveLastWeekRanking() {
    LocalDate today = LocalDate.now();
    LocalDate lastWeekStart = today.minusWeeks(1);

    String weekKey = studyTimeRankKey(lastWeekStart);

    try {
        rankArchiveService.archiveRanksInPages(weekKey, lastWeekStart);
        redisTemplate.delete(weekKey);
        log.info("[RankBatchScheduler] Archive complete and Redis key '{}' deleted.", weekKey);
    } catch (Exception e) {
        log.error("[RankBatchScheduler] Archive failed for key '{}'. Error: {}", weekKey, e.getMessage(), e);
    }
}
}
