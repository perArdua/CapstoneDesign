package com.example.campusin.application.rank.mirror;

import com.example.campusin.domain.rank.dto.response.RankListResponse;
import com.example.campusin.mirror.MirrorComparator;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RankComparator implements MirrorComparator<Page<RankListResponse>> {

    @Override
    public String compare(Page<RankListResponse> primary, Page<RankListResponse> shadow) {
        if (primary == null || shadow == null) {
            return "NULL_PAGE";
        }
        if (primary.getTotalElements() != shadow.getTotalElements()) {
            return "SIZE_MISMATCH";
        }
        List<RankListResponse> p = primary.getContent();
        List<RankListResponse> s = shadow.getContent();
        if (p.size() != s.size()) {
            return "PAGE_SIZE_MISMATCH";
        }
        for (int i = 0; i < p.size(); i++) {
            RankListResponse a = p.get(i);
            RankListResponse b = s.get(i);
            if (!Objects.equals(a.getName(), b.getName())) return "NAME_DIFF@" + i;
            if (!Objects.equals(a.getRank(), b.getRank())) return "RANK_DIFF@" + i;
            if (a.getWeek() != b.getWeek()) return "WEEK_DIFF@" + i;
            if (a.getMonth() != b.getMonth()) return "MONTH_DIFF@" + i;
            Double pa = a.getScore();
            Double pb = b.getScore();
            if (pa != null && pb != null && Double.compare(pa, pb) != 0) {
                return "SCORE_DIFF@" + i;
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> summarize(Page<RankListResponse> page) {
        if (page == null) {
            return Map.of();
        }
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", page.getTotalElements());
        summary.put("page", page.getNumber());
        summary.put("size", page.getSize());
        summary.put("sample", page.getContent().stream()
                .limit(5)
                .map(this::toSample)
                .toList());
        return summary;
    }

    @Override
    public String digest(Page<RankListResponse> page) {
        if (page == null) {
            return null;
        }
        return page.getContent().stream()
                .limit(5)
                .map(RankListResponse::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(","));
    }

    private Map<String, Object> toSample(RankListResponse item) {
        Map<String, Object> sample = new HashMap<>();
        sample.put("rank", item.getRank());
        sample.put("name", item.getName());
        sample.put("score", item.getScore());
        return sample;
    }
}
