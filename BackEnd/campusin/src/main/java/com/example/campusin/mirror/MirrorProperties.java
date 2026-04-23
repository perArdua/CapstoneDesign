package com.example.campusin.mirror;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mirror")
public class MirrorProperties {
    private boolean enabled = true;
    // yml: mirror.sample-rate (kebab) ↔ Java: sampleRate (camel). Relaxed Binding 매칭용.
    // 이전 samplingRate 표기는 바인딩되지 않아 yml 값이 무시되는 버그가 있었음.
    private double sampleRate = 1.0; // 0..1
    private long timeoutMs = 2000;
    private int queueCapacity = 100;
    private int corePoolSize = 2;
    private int maxPoolSize = 4;
    private int keepAliveSeconds = 60;
    @Deprecated
    private long maxPayloadSize = 16_384; // bytes (reserved for future payload logging)
    private boolean alwaysLog = true;
    private String shadowPrefix = "shadow:";
    private Async async = new Async();

    @PostConstruct
    public void logConfig() {
        log.info(
                "Mirror config bound: enabled={}, sampleRate={}, timeoutMs={}, shadowPrefix={}, alwaysLog={}",
                enabled, sampleRate, timeoutMs, shadowPrefix, alwaysLog
        );
    }

    @Getter
    @Setter
    public static class Async {
        private Shadow shadow = new Shadow();
    }

    @Getter
    @Setter
    public static class Shadow {
        private int queueCapacity = 50;
        private int corePoolSize = 1;
        private int maxPoolSize = 2;
        private int keepAliveSeconds = 60;
    }
}
