package com.example.campusin.application.rank;

import com.example.campusin.domain.rank.Ranks;
import com.example.campusin.domain.rank.dto.response.RankListResponse;
import com.example.campusin.infra.rank.RankRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankWarmUpService {

    private static final int PAGE_SIZE = 20;

    private final RankRepository rankRepository;
    private final RankCacheService rankCacheService;
    private final ObjectMapper objectMapper;

    // 과거 주차 조회가 폭증하는 월요일 직후에도 락 없이 cache hit로 흡수되도록,
    // 배치 시점에 상위 N개 페이지를 Redis에 미리 적재한다.
    public void warmUpPopularPages(LocalDate weekStart, int topPageCount) {
        for (int page = 0; page < topPageCount; page++) {
            try {
                warmUpSinglePage(weekStart, page);
            } catch (Exception e) {
                // 특정 페이지 실패가 나머지 페이지를 막지 않도록 격리한다.
                log.error("[RankWarmUpService] warm-up failed. weekStart={}, page={}, error={}",
                        weekStart, page, e.getMessage(), e);
            }
        }
    }

    private void warmUpSinglePage(LocalDate weekStart, int page) throws Exception {
        Page<Ranks> ranks = rankRepository.findAllByWeekStartDateOrderByStudyRankingAsc(
                weekStart, PageRequest.of(page, PAGE_SIZE));
        List<RankListResponse> responses = ranks.getContent().stream()
                .map(RankListResponse::new)
                .toList();

        String json = objectMapper.writeValueAsString(responses);
        rankCacheService.cacheWeeklyRankPage(weekStart, page, json);
        log.info("[RankWarmUpService] warm-up succeeded. weekStart={}, page={}, size={}",
                weekStart, page, responses.size());
    }
}
