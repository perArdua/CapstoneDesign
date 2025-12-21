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
    @DisplayName("점수가 다르면 SCORE_DIFF")
    void score_diff() {
        Page<RankListResponse> p = pageWithScore(new double[]{10.0, 9.0});
        Page<RankListResponse> s = pageWithScore(new double[]{10.0, 8.0});
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
}
