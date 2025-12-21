package com.example.campusin.mirror;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "rank-mirror")
@Component
@RequiredArgsConstructor
public class MirrorLogger {

    private final ObjectMapper objectMapper;

    public void log(MirrorContext context, MirrorResult<?> result) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("ts", Instant.now().toString());
            payload.put("corr", context.getCorrelationId());
            payload.put("api", context.getApiName());
            payload.put("params", context.getParamsSummary());
            payload.put("status", result.getStatus());
            payload.put("diffType", result.getDiffType());
            payload.put("pLatencyMs", result.getPrimaryLatencyMs());
            payload.put("sLatencyMs", result.getShadowLatencyMs());
            payload.put("error", result.getErrorMessage());
            payload.put("primarySummary", result.getPrimarySummary());
            payload.put("shadowSummary", result.getShadowSummary());
            payload.put("primaryDigest", result.getPrimaryDigest());
            payload.put("shadowDigest", result.getShadowDigest());
            log.info(objectMapper.writeValueAsString(payload));
        } catch (Exception e) {
            log.warn("Failed to write mirror log", e);
        }
    }
}
