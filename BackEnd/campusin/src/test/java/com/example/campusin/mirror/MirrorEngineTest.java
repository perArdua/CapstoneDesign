package com.example.campusin.mirror;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskRejectedException;

import java.util.Map;

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

    private MirrorEngine mirrorEngine;

    @BeforeEach
    void setUp() {
        mirrorProperties = new MirrorProperties();
        mirrorProperties.setSamplingRate(1.0);
        mirrorProperties.setEnabled(true);
        mirrorEngine = new MirrorEngine(mirrorProperties, mirrorWorker);
    }

    @Nested
    @DisplayName("submit 메서드는")
    class Describe_submit {

        @Test
        @DisplayName("mirror 비활성화 시 worker를 호출하지 않는다")
        void skip_when_disabled() {
            // given
            mirrorProperties.setEnabled(false);

            // when
            mirrorEngine.submit("api", Map.of(), () -> "p", () -> "s", (p, s) -> null);

            // then
            verifyNoInteractions(mirrorWorker);
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
