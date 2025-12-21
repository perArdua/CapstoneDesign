package com.example.campusin.mirror;

import java.util.Map;

public interface MirrorComparator<R> {
    /**
     * @return null if equivalent, or diffType string if different.
     */
    String compare(R primary, R shadow);

    /**
     * 도메인별 요약 정보를 반환한다. 필요한 경우 topN 제한 등은 구현체에서 처리.
     */
    default Map<String, Object> summarize(R data) {
        return Map.of();
    }

    /**
     * 요약 문자열(옵션). 없으면 null.
     */
    default String digest(R data) {
        return null;
    }
}
