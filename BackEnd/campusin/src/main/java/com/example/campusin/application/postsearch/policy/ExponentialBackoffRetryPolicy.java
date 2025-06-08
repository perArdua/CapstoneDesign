package com.example.campusin.application.postsearch.policy;

import org.springframework.stereotype.Component;

import java.time.Duration;

public class ExponentialBackoffRetryPolicy implements BulkIndexRetryPolicy {
    private final int maxRetries;
    private final Duration initialBackoff;

    public ExponentialBackoffRetryPolicy(int maxRetries, Duration initialBackoff) {
        this.maxRetries = maxRetries;
        this.initialBackoff = initialBackoff;
    }

    @Override
    public boolean shouldRetry(int retryCount, int statusCode) {
        return retryCount < maxRetries && statusCode == 429;
    }

    @Override
    public Duration getBackoffDuration(int retryCount) {
        return initialBackoff.multipliedBy((long) Math.pow(2, retryCount));
    }
}
