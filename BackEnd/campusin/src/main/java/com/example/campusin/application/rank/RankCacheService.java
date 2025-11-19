package com.example.campusin.application.rank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static com.example.campusin.common.redis.RedisKeyFactory.weeklyRankPageKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String CACHE_KEY_SET = "cache:rank:weekly:pageKeys";
    private static final int MAX_CACHE_SIZE = 100;

    public void cacheWeeklyRankPage(LocalDate weekStartDate, int page, String jsonValue) {
        String fullPageKey = weeklyRankPageKey(weekStartDate, page);
        redisTemplate.opsForValue().set(fullPageKey, jsonValue, Duration.ofDays(1));
        redisTemplate.opsForZSet().add(CACHE_KEY_SET, fullPageKey, System.currentTimeMillis());

        Long size = redisTemplate.opsForZSet().zCard(CACHE_KEY_SET);
        if (size != null && size > MAX_CACHE_SIZE) {
            Set<String> toRemove = redisTemplate.opsForZSet().range(CACHE_KEY_SET, 0, size - MAX_CACHE_SIZE - 1);

            if (toRemove != null) {
                redisTemplate.delete(toRemove);
                redisTemplate.opsForZSet().remove(CACHE_KEY_SET, toRemove.toArray());
                log.info("LRU 캐시 초과로 {}건 제거됨", toRemove.size());
            }
        }
    }

    public String getCachedWeeklyRankPage(LocalDate weekStartDate, int page) {
        return redisTemplate.opsForValue().get(weeklyRankPageKey(weekStartDate, page));
    }

    public void evictPage(LocalDate weekStartDate, int page) {
        String key = weeklyRankPageKey(weekStartDate, page);
        redisTemplate.delete(key);
        redisTemplate.opsForZSet().remove(CACHE_KEY_SET, key);
    }

    public Set<String> getAllCachedKeys() {
        return Optional.ofNullable(redisTemplate.opsForZSet().range(CACHE_KEY_SET, 0, -1))
                .orElse(Set.of());
    }
}
