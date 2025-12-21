package com.example.campusin.mirror;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class MirrorResult<T> {
    public enum Status {
        OK, DIFF, ERROR
    }

    private final Status status;
    private final String diffType;
    private final long primaryLatencyMs;
    private final long shadowLatencyMs;
    private final T primary;
    private final T shadow;
    private final String errorMessage;
    private final Map<String, Object> primarySummary;
    private final Map<String, Object> shadowSummary;
    private final String primaryDigest;
    private final String shadowDigest;
}
