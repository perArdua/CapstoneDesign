package com.example.campusin.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static com.example.campusin.common.redis.RedisKeyFactory.LOCK_PREFIX;

@Component
@RequiredArgsConstructor
public class RedisLockHelper {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean tryLock(String key, String value, Duration expireTime) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(
                LOCK_PREFIX + key, value, expireTime
        ));
    }

    public void unlock(String key, String value) {
        String redisKey = LOCK_PREFIX + key;
        String currentValue = redisTemplate.opsForValue().get(redisKey);

        if (value.equals(currentValue)) {
            redisTemplate.delete(redisKey);
        }
    }
}
