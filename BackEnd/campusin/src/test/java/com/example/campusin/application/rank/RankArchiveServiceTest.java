package com.example.campusin.application.rank;

import com.example.campusin.domain.rank.Ranks;
import com.example.campusin.infra.rank.RankRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.example.campusin.common.redis.RedisKeyFactory.FAILURE_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("RankArchiveService")
class RankArchiveServiceTest {

    @Mock
    RedisTemplate<String, String> redisTemplate;
    @Mock
    RankRepository rankRepository;

    @InjectMocks
    RankArchiveService rankArchiveService;

    @Nested
    @DisplayName("archiveRanksInPages 메서드는")
    class Describe_archiveRanksInPages {

        @Test
        @DisplayName("Redis ZSET을 순회하며 Ranks를 생성해 저장한다")
        void ZSET을_저장한다() {
            // given
            String weekKey = "week:2024-01-01";
            LocalDate weekStartDate = LocalDate.of(2024, 1, 1);

            ZSetOperations<String, String> zSetOperations = mock(ZSetOperations.class);
            given(redisTemplate.opsForZSet()).willReturn(zSetOperations);

            Set<ZSetOperations.TypedTuple<String>> tuples = new LinkedHashSet<>();
            tuples.add(new DefaultTypedTuple<>("alice", 120.0));
            tuples.add(new DefaultTypedTuple<>("bob", 60.0));

            given(zSetOperations.reverseRangeWithScores(weekKey, 0, 499)).willReturn(tuples);

            // when
            rankArchiveService.archiveRanksInPages(weekKey, weekStartDate);

            // then
            ArgumentCaptor<List<Ranks>> ranksCaptor = ArgumentCaptor.forClass(List.class);
            then(rankRepository).should().saveAll(ranksCaptor.capture());
            List<Ranks> saved = ranksCaptor.getValue();
            assertThat(saved).hasSize(2);
            assertThat(saved.get(0).getUserName()).isEqualTo("alice");
            assertThat(saved.get(0).getStudyRanking()).isEqualTo(1L);
            assertThat(saved.get(1).getUserName()).isEqualTo("bob");
            assertThat(saved.get(1).getStudyRanking()).isEqualTo(2L);
        }

        @Test
        @DisplayName("batch 저장 실패 시 실패 큐에 직렬화해 넣는다")
        void 실패_큐에_저장한다() {
            // given
            String weekKey = "week:2024-01-08";
            LocalDate weekStartDate = LocalDate.of(2024, 1, 8);

            ZSetOperations<String, String> zSetOperations = mock(ZSetOperations.class);
            given(redisTemplate.opsForZSet()).willReturn(zSetOperations);

            Set<ZSetOperations.TypedTuple<String>> tuples = new LinkedHashSet<>();
            tuples.add(new DefaultTypedTuple<>("fail", 30.0));

            given(zSetOperations.reverseRangeWithScores(weekKey, 0, 499)).willReturn(tuples);
            doThrow(new RuntimeException("save error")).when(rankRepository).saveAll(any());

            ListOperations<String, String> listOperations = mock(ListOperations.class);
            given(redisTemplate.opsForList()).willReturn(listOperations);

            Object mapper = ReflectionTestUtils.getField(rankArchiveService, "objectMapper");
            ((ObjectMapper) mapper).registerModule(new JavaTimeModule());

            // when
            rankArchiveService.archiveRanksInPages(weekKey, weekStartDate);

            // then
            then(listOperations).should().rightPush(eq(FAILURE_KEY), any());
        }
    }

    @Nested
    @DisplayName("saveBatch 메서드는")
    class Describe_saveBatch {

        @Test
        @DisplayName("랭크 배치를 저장한다")
        void 배치를_저장한다() {
            // given
            List<Ranks> batch = List.of(
                    Ranks.builder()
                            .userName("batch-user")
                            .weekStartDate(LocalDate.now())
                            .build());

            // when
            rankArchiveService.saveBatch(batch);

            // then
            then(rankRepository).should().saveAll(batch);
        }
    }

    @Nested
    @DisplayName("saveToFailureQueue 메서드는")
    class Describe_saveToFailureQueue {

        @Test
        @DisplayName("실패한 배치를 직렬화해 Redis 리스트에 쌓는다")
        void 실패를_리스트에_저장한다() {
            // given
            List<Ranks> failedBatch = List.of(
                    Ranks.builder()
                            .userName("fail-user")
                            .weekStartDate(LocalDate.of(2024, 1, 1))
                            .totalElapsedTime(30L)
                            .build());
            ListOperations<String, String> listOperations = mock(ListOperations.class);
            given(redisTemplate.opsForList()).willReturn(listOperations);

            Object mapper = ReflectionTestUtils.getField(rankArchiveService, "objectMapper");
            ((ObjectMapper) mapper).registerModule(new JavaTimeModule());

            // when
            rankArchiveService.saveToFailureQueue(failedBatch);

            // then
            then(listOperations).should().rightPush(eq(FAILURE_KEY), any());
        }
    }
}
