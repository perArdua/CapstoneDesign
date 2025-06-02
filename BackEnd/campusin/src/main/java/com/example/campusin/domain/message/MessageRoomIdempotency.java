package com.example.campusin.domain.message;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "message_room_idempotency",
        uniqueConstraints = @UniqueConstraint(columnNames = {"senderId", "receiverId", "idempotencyKey"}))
public class MessageRoomIdempotency extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private Long receiverId;

    @Column(nullable = false, length = 128)
    private String idempotencyKey;

    @Column(nullable = false)
    private Long messageRoomId;

    public MessageRoomIdempotency(Long senderId, Long receiverId, String idempotencyKey, Long messageRoomId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.idempotencyKey = idempotencyKey;
        this.messageRoomId = messageRoomId;
    }
}
