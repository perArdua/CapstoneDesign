package com.example.campusin.mirror;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class MirrorWorker {

    private final MirrorProperties properties;
    private final MirrorLogger mirrorLogger;
    @Qualifier("shadowTaskExecutor")
    private final AsyncTaskExecutor shadowTaskExecutor;

    @Async("mirrorTaskExecutor")
    public <R> void runMirror(MirrorContext context,
                              Supplier<R> primarySupplier,
                              ShadowRunner<R> shadowRunner,
                              MirrorComparator<R> comparator) {
        R primary = null;
        R shadow = null;
        long primaryLatency = 0;
        long shadowLatency = 0;
        MirrorResult.Status status = MirrorResult.Status.OK;
        String diffType = null;
        String err = null;
        Map<String, Object> primarySummary = Map.of();
        Map<String, Object> shadowSummary = Map.of();
        String primaryDigest = null;
        String shadowDigest = null;

        try {
            long pStart = System.nanoTime();
            primary = primarySupplier.get();
            primaryLatency = toMillis(pStart);

            long sStart = System.nanoTime();
            Future<R> shadowFuture;
            try {
                shadowFuture = shadowTaskExecutor.submit(shadowRunner::run);
            } catch (RejectedExecutionException e) {
                shadowLatency = toMillis(sStart);
                status = MirrorResult.Status.ERROR;
                err = "shadow-rejected";
                shadowFuture = null;
            }

            if (shadowFuture != null) {
                try {
                    shadow = shadowFuture.get(properties.getTimeoutMs(), TimeUnit.MILLISECONDS);
                    shadowLatency = toMillis(sStart);

                    diffType = comparator.compare(primary, shadow);
                    if (diffType != null) {
                        status = MirrorResult.Status.DIFF;
                    }
                    primarySummary = comparator.summarize(primary);
                    shadowSummary = comparator.summarize(shadow);
                    primaryDigest = comparator.digest(primary);
                    shadowDigest = comparator.digest(shadow);
                } catch (TimeoutException e) {
                    shadowLatency = toMillis(sStart);
                    status = MirrorResult.Status.ERROR;
                    err = "shadow-timeout";
                    shadowFuture.cancel(true);
                } catch (ExecutionException e) {
                    shadowLatency = toMillis(sStart);
                    status = MirrorResult.Status.ERROR;
                    err = errorMessage(e.getCause());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    shadowLatency = toMillis(sStart);
                    status = MirrorResult.Status.ERROR;
                    err = "interrupted";
                }
            }
        } catch (Exception e) {
            status = MirrorResult.Status.ERROR;
            err = errorMessage(e);
        }

        MirrorResult<R> result = MirrorResult.<R>builder()
                .status(status)
                .diffType(diffType)
                .primaryLatencyMs(primaryLatency)
                .shadowLatencyMs(shadowLatency)
                .primary(primary)
                .shadow(shadow)
                .errorMessage(err)
                .primarySummary(primarySummary)
                .shadowSummary(shadowSummary)
                .primaryDigest(primaryDigest)
                .shadowDigest(shadowDigest)
                .build();

        if (properties.isAlwaysLog() || status != MirrorResult.Status.OK) {
            mirrorLogger.log(context, result);
        }
    }

    private long toMillis(long startNano) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNano);
    }

    private String errorMessage(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        return throwable.getMessage();
    }
}
