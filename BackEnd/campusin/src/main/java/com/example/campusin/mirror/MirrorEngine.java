package com.example.campusin.mirror;

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

    private final MirrorProperties properties;
    private final MirrorWorker mirrorWorker;

    public <R> void submit(String apiName,
                           Map<String, Object> paramsSummary,
                           Supplier<R> primarySupplier,
                           ShadowRunner<R> shadowRunner,
                           MirrorComparator<R> comparator) {
        if (!properties.isEnabled()
                || ThreadLocalRandom.current().nextDouble() > properties.getSamplingRate()) {
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
}
