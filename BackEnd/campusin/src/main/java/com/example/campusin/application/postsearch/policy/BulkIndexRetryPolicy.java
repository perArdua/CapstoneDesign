package com.example.campusin.application.postsearch.policy;

import java.time.Duration;

public interface BulkIndexRetryPolicy {
    boolean shouldRetry(int retryCount, int statusCode);
    Duration getBackoffDuration(int retryCount);
}
