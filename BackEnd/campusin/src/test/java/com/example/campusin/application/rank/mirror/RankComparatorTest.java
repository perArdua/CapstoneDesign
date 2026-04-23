package com.example.campusin.application.rank.mirror;

import com.example.campusin.domain.rank.dto.response.RankListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RankComparator")
class RankComparatorTest {

    RankComparator comparator = new RankComparator();

    @Test
    @DisplayName("동일 순서/값이면 diff 없음")
    void same_order() {
        Page<RankListResponse> p = page("alice", "bob");
        Page<RankListResponse> s = page("alice", "bob");
        assertThat(comparator.compare(p, s)).isNull();
    }

    @Test
    @DisplayName("총 요소 수 다르면 SIZE_MISMATCH")
    void size_mismatch() {
        Page<RankListResponse> p = page("alice", "bob");
        Page<RankListResponse> s = page("alice");
        assertThat(comparator.compare(p, s)).isEqualTo("SIZE_MISMATCH");
    }

    @Test
    @DisplayName("순서가 다르면 NAME_DIFF@index")
    void order_diff() {
        Page<RankListResponse> p = page("alice", "bob");
        Page<RankListResponse> s = page("bob", "alice");
        assertThat(comparator.compare(p, s)).startsWith("NAME_DIFF@");
    }

    @Test
    @DisplayName("점수 차이가 허용 오차 초과면 SCORE_DIFF")
    void score_diff() {
        Page<RankListResponse> p = pageWithScore(new double[]{10.0, 9.0});
        Page<RankListResponse> s = pageWithScore(new double[]{10.0, 8.0});
        assertThat(comparator.compare(p, s)).startsWith("SCORE_DIFF@");
    }

    @Test
    @DisplayName("양쪽 score가 모두 null이면 diff 없음")
    void score_null_on_both_sides() {
        Page<RankListResponse> p = pageWithScoreBoxed(new Double[]{10.0, null});
        Page<RankListResponse> s = pageWithScoreBoxed(new Double[]{10.0, null});
        assertThat(comparator.compare(p, s)).isNull();
    }

    @Test
    @DisplayName("한쪽 score만 null이면 SCORE_NULL_DIFF")
    void score_null_on_one_side() {
        Page<RankListResponse> p = pageWithScoreBoxed(new Double[]{10.0, null});
        Page<RankListResponse> s = pageWithScoreBoxed(new Double[]{10.0, 8.0});
        assertThat(comparator.compare(p, s)).startsWith("SCORE_NULL_DIFF@");
    }

    @Test
    @DisplayName("부동소수점 미세 오차(0.00005)는 허용 오차 이내 → diff 없음")
    void score_within_tolerance() {
        // SCORE_TOLERANCE = 0.0001. 0.00005 < 0.0001 → 일치 판정.
        // 근거: 정규화 후 Math.floor 결과의 double 표현에서 발생할 수 있는 미세 오차를 모사.
        Page<RankListResponse> p = pageWithScore(new double[]{10.0, 9.0});
        Page<RankListResponse> s = pageWithScore(new double[]{10.0, 9.00005});
        assertThat(comparator.compare(p, s)).isNull();
    }

    @Test
    @DisplayName("의미 있는 차이(0.5)는 허용 오차 초과 → SCORE_DIFF")
    void score_outside_tolerance() {
        // SCORE_TOLERANCE = 0.0001. 0.5 >> 0.0001 → 불일치 판정.
        // 1분 미만(정규화 단위) 차이라도 허용 오차를 초과하므로 감지돼야 함.
        Page<RankListResponse> p = pageWithScore(new double[]{10.0, 9.0});
        Page<RankListResponse> s = pageWithScore(new double[]{10.0, 9.5});
        assertThat(comparator.compare(p, s)).startsWith("SCORE_DIFF@");
    }

    private Page<RankListResponse> page(String... names) {
        List<RankListResponse> list = new java.util.ArrayList<>();
        long rank = 1;
        for (String name : names) {
            list.add(RankListResponse.builder().name(name).rank(rank++).build());
        }
        return new PageImpl<>(list, PageRequest.of(0, list.size()), list.size());
    }

    private Page<RankListResponse> pageWithScore(double[] scores) {
        List<RankListResponse> list = new java.util.ArrayList<>();
        long rank = 1;
        for (double score : scores) {
            list.add(RankListResponse.builder().name("u" + rank).rank(rank).score(score).build());
            rank++;
        }
        return new PageImpl<>(list, PageRequest.of(0, list.size()), list.size());
    }

    private Page<RankListResponse> pageWithScoreBoxed(Double[] scores) {
        List<RankListResponse> list = new java.util.ArrayList<>();
        long rank = 1;
        for (Double score : scores) {
            list.add(RankListResponse.builder().name("u" + rank).rank(rank).score(score).build());
            rank++;
        }
        return new PageImpl<>(list, PageRequest.of(0, list.size()), list.size());
    }
}
