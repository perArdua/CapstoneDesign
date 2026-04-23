package com.example.campusin.application.rank;

import com.example.campusin.domain.rank.Ranks;
import com.example.campusin.infra.rank.RankRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RankWarmUpService")
class RankWarmUpServiceTest {

    @Mock
    RankRepository rankRepository;
    @Mock
    RankCacheService rankCacheService;
    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    RankWarmUpService rankWarmUpService;

    @Nested
    @DisplayName("warmUpPopularPages 메서드는")
    class Describe_warmUpPopularPages {

        @Test
        @DisplayName("상위 N개 페이지를 조회해 Redis에 미리 적재한다")
        void 상위_N개_페이지를_적재한다() throws Exception {
            // given
            LocalDate weekStart = LocalDate.of(2024, 1, 1);
            Ranks ranks = Ranks.builder()
                    .userName("alice")
                    .weekStartDate(weekStart)
                    .studyRanking(1L)
                    .totalElapsedTime(300L)
                    .build();
            Page<Ranks> ranksPage = new PageImpl<>(List.of(ranks));
            given(rankRepository.findAllByWeekStartDateOrderByStudyRankingAsc(eq(weekStart), any(PageRequest.class)))
                    .willReturn(ranksPage);
            given(objectMapper.writeValueAsString(any())).willReturn("[{\"name\":\"alice\"}]");

            // when
            rankWarmUpService.warmUpPopularPages(weekStart, 3);

            // then
            ArgumentCaptor<Integer> pageCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(rankCacheService, times(3))
                    .cacheWeeklyRankPage(eq(weekStart), pageCaptor.capture(), anyString());
            assertThat(pageCaptor.getAllValues()).containsExactly(0, 1, 2);
        }

        @Test
        @DisplayName("특정 페이지 조회가 실패해도 나머지 페이지는 계속 처리한다")
        void 일부_실패해도_계속() throws Exception {
            // given
            LocalDate weekStart = LocalDate.of(2024, 2, 1);
            Ranks ranks = Ranks.builder()
                    .userName("bob")
                    .weekStartDate(weekStart)
                    .studyRanking(1L)
                    .totalElapsedTime(100L)
                    .build();
            Page<Ranks> ranksPage = new PageImpl<>(List.of(ranks));

            // page 0 성공, page 1 실패, page 2 성공
            given(rankRepository.findAllByWeekStartDateOrderByStudyRankingAsc(
                    eq(weekStart), eq(PageRequest.of(0, 20)))).willReturn(ranksPage);
            willThrow(new RuntimeException("db down"))
                    .given(rankRepository)
                    .findAllByWeekStartDateOrderByStudyRankingAsc(eq(weekStart), eq(PageRequest.of(1, 20)));
            given(rankRepository.findAllByWeekStartDateOrderByStudyRankingAsc(
                    eq(weekStart), eq(PageRequest.of(2, 20)))).willReturn(ranksPage);
            given(objectMapper.writeValueAsString(any())).willReturn("[]");

            // when
            rankWarmUpService.warmUpPopularPages(weekStart, 3);

            // then
            verify(rankCacheService).cacheWeeklyRankPage(eq(weekStart), eq(0), anyString());
            verify(rankCacheService, never()).cacheWeeklyRankPage(eq(weekStart), eq(1), anyString());
            verify(rankCacheService).cacheWeeklyRankPage(eq(weekStart), eq(2), anyString());
        }

        @Test
        @DisplayName("직렬화에 실패한 페이지는 캐시에 저장하지 않고 다음 페이지로 넘어간다")
        void 직렬화_실패시_해당_페이지만_스킵() throws JsonProcessingException {
            // given
            LocalDate weekStart = LocalDate.of(2024, 3, 1);
            Ranks ranks = Ranks.builder()
                    .userName("carol")
                    .weekStartDate(weekStart)
                    .studyRanking(1L)
                    .totalElapsedTime(50L)
                    .build();
            Page<Ranks> ranksPage = new PageImpl<>(List.of(ranks));
            given(rankRepository.findAllByWeekStartDateOrderByStudyRankingAsc(eq(weekStart), any(PageRequest.class)))
                    .willReturn(ranksPage);
            given(objectMapper.writeValueAsString(any()))
                    .willThrow(new JsonProcessingException("serialize fail") {});

            // when
            rankWarmUpService.warmUpPopularPages(weekStart, 2);

            // then
            verify(rankCacheService, never()).cacheWeeklyRankPage(any(), anyInt(), anyString());
        }
    }
}
