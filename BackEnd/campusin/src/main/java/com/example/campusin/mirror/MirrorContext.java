package com.example.campusin.mirror;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@AllArgsConstructor
public class MirrorContext {
    private final String correlationId;
    private final String apiName;
    private final Map<String, Object> paramsSummary;
    private final Instant timestamp;

    public static MirrorContext of(String correlationId,
                                   String apiName,
                                   Map<String, Object> paramsSummary,
                                   Instant timestamp) {
        return new MirrorContext(correlationId, apiName, paramsSummary, timestamp);
    }
}
