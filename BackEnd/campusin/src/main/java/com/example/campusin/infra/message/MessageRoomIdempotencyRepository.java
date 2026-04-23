package com.example.campusin.infra.message;

import com.example.campusin.domain.message.MessageRoomIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MessageRoomIdempotencyRepository extends JpaRepository<MessageRoomIdempotency, Long> {
    Optional<MessageRoomIdempotency> findBySenderIdAndReceiverIdAndIdempotencyKey(Long senderId, Long receiverId, String idempotencyKey);

    @Modifying
    @Query(value = "DELETE FROM message_room_idempotency WHERE created_at < :cutoff LIMIT :batchSize",
            nativeQuery = true)
    int deleteExpired(@Param("cutoff") LocalDateTime cutoff,
                      @Param("batchSize") int batchSize);
}
