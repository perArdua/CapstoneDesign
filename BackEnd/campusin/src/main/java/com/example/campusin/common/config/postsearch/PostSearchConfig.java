package com.example.campusin.common.config.postsearch;

import com.example.campusin.application.postsearch.policy.ExponentialBackoffRetryPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class PostSearchConfig {

    @Bean
    public ExponentialBackoffRetryPolicy exponentialBackoffRetryPolicy() {
        return new ExponentialBackoffRetryPolicy(5, Duration.ofSeconds(1));
    }
}
