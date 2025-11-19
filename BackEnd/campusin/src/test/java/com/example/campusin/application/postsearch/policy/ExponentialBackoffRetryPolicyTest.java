package com.example.campusin.application.postsearch.policy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ExponentialBackoffRetryPolicy")
class ExponentialBackoffRetryPolicyTest {

    ExponentialBackoffRetryPolicy policy = new ExponentialBackoffRetryPolicy(3, Duration.ofSeconds(1));

    @Test
    @DisplayName("429 상태이며 최대 재시도 미만이면 재시도한다")
    void 재시도_결정() {
        // given // when // then
        assertThat(policy.shouldRetry(0, 429)).isTrue();
        assertThat(policy.shouldRetry(2, 429)).isTrue();
        assertThat(policy.shouldRetry(3, 429)).isFalse();
        assertThat(policy.shouldRetry(0, 500)).isFalse();
    }

    @Test
    @DisplayName("재시도 횟수에 따라 backoff를 2배씩 늘린다")
    void 지수_백오프() {
        // given // when // then
        assertThat(policy.getBackoffDuration(0)).isEqualTo(Duration.ofSeconds(1));
        assertThat(policy.getBackoffDuration(1)).isEqualTo(Duration.ofSeconds(2));
        assertThat(policy.getBackoffDuration(2)).isEqualTo(Duration.ofSeconds(4));
    }
}
