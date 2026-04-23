package com.example.campusin.mirror;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskRejectedException;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("MirrorWorker")
class MirrorWorkerTest {

    @Mock
    private MirrorLogger mirrorLogger;

    private MirrorProperties properties;
    private AsyncTaskExecutor shadowTaskExecutor;
    private MeterRegistry meterRegistry;
    private MirrorWorker mirrorWorker;
    private MirrorContext context;

    @BeforeEach
    void setUp() {
        properties = new MirrorProperties();
        properties.setTimeoutMs(10);
        properties.setSampleRate(1.0);
        properties.setAlwaysLog(true);
        shadowTaskExecutor = new SimpleAsyncTaskExecutor();
        meterRegistry = new SimpleMeterRegistry();
        mirrorWorker = new MirrorWorker(properties, mirrorLogger, shadowTaskExecutor, meterRegistry);
        context = MirrorContext.of("corr", "api", Map.of(), Instant.now());
    }

    @Nested
    @DisplayName("runMirror 메서드는")
    class Describe_runMirror {

        @Test
        @DisplayName("shadow timeout이 발생해도 primary는 실행된다")
        void primary_runs_on_timeout() {
            // given
            AtomicBoolean primaryRun = new AtomicBoolean(false);
            AtomicBoolean shadowInterrupted = new AtomicBoolean(false);

            // when
            mirrorWorker.runMirror(
                    context,
                    () -> {
                        primaryRun.set(true);
                        return "p";
                    },
                    () -> {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            shadowInterrupted.set(true);
                            Thread.currentThread().interrupt();
                        }
                        return "s";
                    },
                    (p, s) -> null
            );

            // then
            assertThat(primaryRun).isTrue();
            assertThat(shadowInterrupted).isTrue();
            verify(mirrorLogger).log(eq(context), argThat(result ->
                    result.getStatus() == MirrorResult.Status.ERROR
                            && "shadow-timeout".equals(result.getErrorMessage())));
        }

        @Test
        @DisplayName("shadow 제출이 거부되면 rejected 상태로 로그한다")
        void logs_rejected_when_shadow_submit_fails() {
            // given
            AsyncTaskExecutor rejectingExecutor = new AsyncTaskExecutor() {
                @Override
                public void execute(Runnable task, long startTimeout) {
                    throw new TaskRejectedException("reject");
                }

                @Override
                public void execute(Runnable task) {
                    throw new TaskRejectedException("reject");
                }

                @Override
                public Future<?> submit(Runnable task) {
                    throw new TaskRejectedException("reject");
                }

                @Override
                public <T> Future<T> submit(Callable<T> task) {
                    throw new TaskRejectedException("reject");
                }
            };
            mirrorWorker = new MirrorWorker(properties, mirrorLogger, rejectingExecutor, meterRegistry);

            // when
            mirrorWorker.runMirror(
                    context,
                    () -> "primary",
                    () -> "shadow",
                    (p, s) -> null
            );

            // then
            verify(mirrorLogger).log(eq(context), argThat(result ->
                    result.getStatus() == MirrorResult.Status.ERROR
                            && "shadow-rejected".equals(result.getErrorMessage())));
        }

        @Test
        @DisplayName("비교 결과가 다르면 DIFF 상태로 로그한다")
        void logs_diff_when_comparator_detects() {
            // given
            properties.setTimeoutMs(1000);

            // when
            mirrorWorker.runMirror(
                    context,
                    () -> "primary",
                    () -> "shadow",
                    new MirrorComparator<>() {
                        @Override
                        public String compare(String primary, String shadow) {
                            return "body";
                        }

                        @Override
                        public Map<String, Object> summarize(String data) {
                            return Map.of("value", data);
                        }

                        @Override
                        public String digest(String data) {
                            return data;
                        }
                    }
            );

            // then
            verify(mirrorLogger).log(eq(context), argThat(result ->
                    result.getStatus() == MirrorResult.Status.DIFF
                            && "body".equals(result.getDiffType())
                            && "primary".equals(result.getPrimaryDigest())
                            && "shadow".equals(result.getShadowDigest())
                            && result.getPrimarySummary().get("value").equals("primary")
                            && result.getShadowSummary().get("value").equals("shadow")));
        }
    }
}
