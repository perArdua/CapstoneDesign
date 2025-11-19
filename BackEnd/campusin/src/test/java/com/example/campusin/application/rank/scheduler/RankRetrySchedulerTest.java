package com.example.campusin.application.rank.scheduler;

import com.example.campusin.domain.rank.Ranks;
import com.example.campusin.infra.rank.RankRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static com.example.campusin.common.redis.RedisKeyFactory.FAILURE_KEY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RankRetryScheduler")
class RankRetrySchedulerTest {

    @Mock
    RedisTemplate<String, String> redisTemplate;
    @Mock
    RankRepository rankRepository;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    ListOperations<String, String> listOperations;

    @InjectMocks
    RankRetryScheduler rankRetryScheduler;

    @Nested
    @DisplayName("retryFailedBatches 메서드는")
    class Describe_retryFailedBatches {

        @Test
        @DisplayName("리스트에서 실패 배치를 꺼내 저장한다")
        void 실패_배치를_재시도한다() throws Exception {
            // given
            String json = "[{}]";
            Ranks ranks = Ranks.builder().userName("u").build();

            given(redisTemplate.opsForList()).willReturn(listOperations);
            given(listOperations.leftPop(FAILURE_KEY)).willReturn(json).willReturn(null);
            given(objectMapper.readValue(json, Ranks[].class)).willReturn(new Ranks[]{ranks});

            // when
            rankRetryScheduler.retryFailedBatches();

            // then
            verify(rankRepository).saveAll(List.of(ranks));
            verify(listOperations, never()).rightPush(any(), any());
        }

        @Test
        @DisplayName("재시도가 실패하면 다시 큐에 넣고 종료한다")
        void 실패시_재큐잉한다() throws Exception {
            // given
            String json = "[{}]";
            given(redisTemplate.opsForList()).willReturn(listOperations);
            given(listOperations.leftPop(FAILURE_KEY)).willReturn(json).willReturn(null);
            given(objectMapper.readValue(json, Ranks[].class)).willThrow(new IllegalStateException("parse error"));

            // when
            rankRetryScheduler.retryFailedBatches();

            // then
            verify(rankRepository, never()).saveAll(any());
            verify(listOperations).rightPush(FAILURE_KEY, json);
        }
    }
}
