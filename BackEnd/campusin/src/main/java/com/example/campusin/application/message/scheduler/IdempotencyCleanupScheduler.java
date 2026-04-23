package com.example.campusin.application.message.scheduler;

import com.example.campusin.infra.message.MessageRoomIdempotencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotencyCleanupScheduler {

    private static final int BATCH_SIZE = 1000;
    private static final long RETENTION_HOURS = 24L;

    private final MessageRoomIdempotencyRepository messageRoomIdempotencyRepository;

    @Scheduled(cron = "0 0 2-6 * * *")
    @Transactional
    public void cleanupExpired() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(RETENTION_HOURS);
        int total = 0;
        int deleted;
        do {
            deleted = messageRoomIdempotencyRepository.deleteExpired(cutoff, BATCH_SIZE);
            total += deleted;
        } while (deleted > 0);

        log.info("[IdempotencyCleanupScheduler] expired rows deleted: total={}, cutoff={}", total, cutoff);
    }
}
