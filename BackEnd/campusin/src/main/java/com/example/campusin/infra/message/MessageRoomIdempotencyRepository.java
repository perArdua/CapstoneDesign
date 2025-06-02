package com.example.campusin.infra.message;

import com.example.campusin.domain.message.MessageRoomIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRoomIdempotencyRepository extends JpaRepository<MessageRoomIdempotency, Long> {
    Optional<MessageRoomIdempotency> findBySenderIdAndReceiverIdAndIdempotencyKey(Long senderId, Long receiverId, String idempotencyKey);
}
