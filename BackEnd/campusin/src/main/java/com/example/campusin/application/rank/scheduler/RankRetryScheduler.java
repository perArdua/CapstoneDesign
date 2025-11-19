package com.example.campusin.application.rank.scheduler;

import com.example.campusin.domain.rank.Ranks;
import com.example.campusin.infra.rank.RankRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.example.campusin.common.redis.RedisKeyFactory.FAILURE_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankRetryScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final RankRepository rankRepository;
    private final ObjectMapper objectMapper;

    private static final int MAX_RETRIES = 3;

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void retryFailedBatches() {
        log.info("[RankRetryScheduler] Starting retry of failed batches...");

        while (true) {
            String json = redisTemplate.opsForList().leftPop(FAILURE_KEY);
            if (json == null) {
                log.info("[RankRetryScheduler] No more failed batches to retry.");
                break;
            }

            try {
                List<Ranks> batch = Arrays.asList(objectMapper.readValue(json, Ranks[].class));
                rankRepository.saveAll(batch);
                log.info("[RankRetryScheduler] Successfully retried failed batch of size {}", batch.size());

            } catch (Exception e) {
                log.error("[RankRetryScheduler] Failed to retry batch. Requeuing. Error: {}", e.getMessage(), e);

                redisTemplate.opsForList().rightPush(FAILURE_KEY, json);
                break;
            }
        }
    }
}
