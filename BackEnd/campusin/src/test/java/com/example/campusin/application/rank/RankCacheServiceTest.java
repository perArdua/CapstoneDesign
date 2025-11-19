package com.example.campusin.application.rank;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDate;
import java.util.Set;

import static com.example.campusin.common.redis.RedisKeyFactory.weeklyRankPageKey;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RankCacheService")
class RankCacheServiceTest {

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    RankCacheService rankCacheService;

    @Nested
    @DisplayName("cacheWeeklyRankPage 메서드는")
    class Describe_cacheWeeklyRankPage {

        @Test
        @DisplayName("페이지를 캐시하고 ZSET에 추가한다")
        void 캐시한다() {
            // given
            LocalDate week = LocalDate.of(2024, 1, 1);
            int page = 1;
            String key = weeklyRankPageKey(week, page);

            ValueOperations<String, String> valueOps = mock(ValueOperations.class);
            ZSetOperations<String, String> zSetOps = mock(ZSetOperations.class);
            when(redisTemplate.opsForValue()).thenReturn(valueOps);
            when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
            when(zSetOps.zCard("cache:rank:weekly:pageKeys")).thenReturn(1L);

            // when
            rankCacheService.cacheWeeklyRankPage(week, page, "{\"r\":1}");

            // then
            verify(valueOps).set(eq(key), eq("{\"r\":1}"), any());
            verify(zSetOps).add(eq("cache:rank:weekly:pageKeys"), eq(key), any(Double.class));
        }
    }

    @Nested
    @DisplayName("getCachedWeeklyRankPage 메서드는")
    class Describe_getCachedWeeklyRankPage {

        @Test
        @DisplayName("키로 저장된 값을 반환한다")
        void 반환한다() {
            // given
            LocalDate week = LocalDate.of(2024, 1, 1);
            String key = weeklyRankPageKey(week, 0);
            ValueOperations<String, String> valueOps = mock(ValueOperations.class);
            when(redisTemplate.opsForValue()).thenReturn(valueOps);
            when(valueOps.get(key)).thenReturn("json");

            // when
            String result = rankCacheService.getCachedWeeklyRankPage(week, 0);

            // then
            assertThat(result).isEqualTo("json");
        }
    }

    @Nested
    @DisplayName("evictPage 메서드는")
    class Describe_evictPage {

        @Test
        @DisplayName("캐시 키와 ZSET 항목을 삭제한다")
        void 삭제한다() {
            // given
            LocalDate week = LocalDate.of(2024, 1, 1);
            String key = weeklyRankPageKey(week, 2);
            ZSetOperations<String, String> zSetOps = mock(ZSetOperations.class);
            when(redisTemplate.opsForZSet()).thenReturn(zSetOps);

            // when
            rankCacheService.evictPage(week, 2);

            // then
            verify(redisTemplate).delete(key);
            verify(zSetOps).remove("cache:rank:weekly:pageKeys", key);
        }
    }

    @Nested
    @DisplayName("getAllCachedKeys 메서드는")
    class Describe_getAllCachedKeys {

        @Test
        @DisplayName("저장된 키 전체를 반환한다")
        void 키목록() {
            // given
            ZSetOperations<String, String> zSetOps = mock(ZSetOperations.class);
            when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
            when(zSetOps.range("cache:rank:weekly:pageKeys", 0, -1)).thenReturn(Set.of("k1", "k2"));

            // when
            Set<String> result = rankCacheService.getAllCachedKeys();

            // then
            assertThat(result).containsExactlyInAnyOrder("k1", "k2");
        }

        @Test
        @DisplayName("비어 있으면 빈 Set을 반환한다")
        void 빈값() {
            // given
            ZSetOperations<String, String> zSetOps = mock(ZSetOperations.class);
            when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
            when(zSetOps.range("cache:rank:weekly:pageKeys", 0, -1)).thenReturn(null);

            // when
            Set<String> result = rankCacheService.getAllCachedKeys();

            // then
            assertThat(result).isEmpty();
            verify(redisTemplate, never()).delete(any(String.class));
        }
    }
}
