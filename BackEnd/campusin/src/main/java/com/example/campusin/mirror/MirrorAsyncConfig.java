package com.example.campusin.mirror;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@EnableAsync
public class MirrorAsyncConfig {

    @Bean(name = "mirrorTaskExecutor")
    public ThreadPoolTaskExecutor mirrorTaskExecutor(MirrorProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
        executor.setThreadNamePrefix("mirror-");
        executor.setRejectedExecutionHandler((r, ex) -> log.warn("Mirror queue overflow; dropping task"));
        executor.initialize();
        return executor;
    }

    @Bean(name = "shadowTaskExecutor")
    public ThreadPoolTaskExecutor shadowTaskExecutor(MirrorProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        MirrorProperties.Shadow shadow = properties.getAsync().getShadow();
        executor.setCorePoolSize(shadow.getCorePoolSize());
        executor.setMaxPoolSize(shadow.getMaxPoolSize());
        executor.setQueueCapacity(shadow.getQueueCapacity());
        executor.setKeepAliveSeconds(shadow.getKeepAliveSeconds());
        executor.setThreadNamePrefix("shadow-");
        executor.setRejectedExecutionHandler((r, ex) -> log.warn("Shadow queue overflow; dropping task"));
        executor.initialize();
        return executor;
    }
}
