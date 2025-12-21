package com.example.campusin.mirror;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mirror")
public class MirrorProperties {
    private boolean enabled = true;
    private double samplingRate = 1.0; // 0..1
    private long timeoutMs = 2000;
    private int queueCapacity = 100;
    private int corePoolSize = 2;
    private int maxPoolSize = 4;
    private int keepAliveSeconds = 60;
    private long maxPayloadSize = 16_384; // bytes (for future payload logging)
    private boolean alwaysLog = true;
    private String shadowPrefix = "shadow:";
    private Async async = new Async();

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
