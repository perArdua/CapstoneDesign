package com.example.campusin.common.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisLockHelper")
class RedisLockHelperTest {

    @Mock
    RedisTemplate<String, String> redisTemplate;
    @Mock
    ValueOperations<String, String> valueOperations;

    @InjectMocks
    RedisLockHelper redisLockHelper;

    @Nested
    @DisplayName("tryLock 메서드는")
    class Describe_tryLock {

        @Test
        @DisplayName("SET NX 성공 여부를 반환한다")
        void 락을_시도한다() {
            // given
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.setIfAbsent(eq("lock:test"), eq("v"), eq(Duration.ofSeconds(5)))).willReturn(true);

            // when
            boolean result = redisLockHelper.tryLock("test", "v", Duration.ofSeconds(5));

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("unlock 메서드는")
    class Describe_unlock {

        @Test
        @DisplayName("현재 값이 일치할 때만 삭제한다")
        void 값이_일치할_때만_삭제() {
            // given
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get("lock:test")).willReturn("v");

            // when
            redisLockHelper.unlock("test", "v");

            // then
            verify(redisTemplate).delete("lock:test");
        }

        @Test
        @DisplayName("값이 다르면 삭제하지 않는다")
        void 값_불일치시_미삭제() {
            // given
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get("lock:test")).willReturn("other");

            // when
            redisLockHelper.unlock("test", "v");

            // then
            verify(redisTemplate, never()).delete("lock:test");
        }
    }
}
