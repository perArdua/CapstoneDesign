package com.example.campusin.mirror;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class MirrorEngine {

    private static final String METRIC_SKIPPED = "mirror.skipped";
    private static final String SKIP_DISABLED = "disabled";
    private static final String SKIP_SAMPLED_OUT = "sampled_out";

    private final MirrorProperties properties;
    private final MirrorWorker mirrorWorker;
    private final MeterRegistry meterRegistry;

    public <R> void submit(String apiName,
                           Map<String, Object> paramsSummary,
                           Supplier<R> primarySupplier,
                           ShadowRunner<R> shadowRunner,
                           MirrorComparator<R> comparator) {
        if (!properties.isEnabled()) {
            recordSkip(apiName, SKIP_DISABLED);
            return;
        }
        if (ThreadLocalRandom.current().nextDouble() > properties.getSampleRate()) {
            recordSkip(apiName, SKIP_SAMPLED_OUT);
            return;
        }

        MirrorContext context = MirrorContext.of(
                UUID.randomUUID().toString(),
                apiName,
                paramsSummary,
                java.time.Instant.now()
        );

        try {
            mirrorWorker.runMirror(context, primarySupplier, shadowRunner, comparator);
        } catch (TaskRejectedException e) {
            log.warn("Mirror queue overflow for {}", apiName);
        } catch (Exception e) {
            log.warn("Mirror dispatch failed for {}", apiName, e);
        }
    }

    private void recordSkip(String apiName, String reason) {
        if (log.isDebugEnabled()) {
            log.debug("mirror skipped reason={} api={}", reason, apiName);
        }
        Counter.builder(METRIC_SKIPPED)
                .tag("api", apiName)
                .tag("reason", reason)
                .register(meterRegistry)
                .increment();
    }
}
