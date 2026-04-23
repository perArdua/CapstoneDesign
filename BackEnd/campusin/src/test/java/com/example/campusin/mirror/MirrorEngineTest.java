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
import org.springframework.core.task.TaskRejectedException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("MirrorEngine")
class MirrorEngineTest {

    @Mock
    private MirrorWorker mirrorWorker;

    private MirrorProperties mirrorProperties;
    private MeterRegistry meterRegistry;

    private MirrorEngine mirrorEngine;

    @BeforeEach
    void setUp() {
        mirrorProperties = new MirrorProperties();
        mirrorProperties.setSampleRate(1.0);
        mirrorProperties.setEnabled(true);
        meterRegistry = new SimpleMeterRegistry();
        mirrorEngine = new MirrorEngine(mirrorProperties, mirrorWorker, meterRegistry);
    }

    @Nested
    @DisplayName("submit 메서드는")
    class Describe_submit {

        @Test
        @DisplayName("mirror 비활성화 시 worker를 호출하지 않고 disabled 스킵을 기록한다")
        void skip_when_disabled() {
            // given
            mirrorProperties.setEnabled(false);

            // when
            mirrorEngine.submit("api", Map.of(), () -> "p", () -> "s", (p, s) -> null);

            // then
            verifyNoInteractions(mirrorWorker);
            assertThat(meterRegistry.counter("mirror.skipped", "api", "api", "reason", "disabled").count())
                    .isEqualTo(1.0);
        }

        @Test
        @DisplayName("sample-rate=0이면 sampled_out 스킵을 기록한다")
        void skip_when_sampled_out() {
            // given
            mirrorProperties.setSampleRate(0.0);

            // when
            mirrorEngine.submit("api", Map.of(), () -> "p", () -> "s", (p, s) -> null);

            // then
            verifyNoInteractions(mirrorWorker);
            assertThat(meterRegistry.counter("mirror.skipped", "api", "api", "reason", "sampled_out").count())
                    .isEqualTo(1.0);
        }

        @Test
        @DisplayName("큐가 거부되어도 primary 흐름에 예외를 전파하지 않는다")
        void swallow_rejection() {
            // given
            doThrow(new TaskRejectedException("full")).when(mirrorWorker)
                    .runMirror(any(), any(), any(), any());

            // when
            assertThatCode(() -> mirrorEngine.submit(
                    "api", Map.of(), () -> "p", () -> "s", (p, s) -> null))
                    // then
                    .doesNotThrowAnyException();
        }
    }
}
