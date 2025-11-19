package com.example.campusin.infra.message;

import com.example.campusin.domain.message.MessageRoomIdempotency;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MessageRoomIdempotencyRepository")
class MessageRoomIdempotencyRepositoryTest extends DataJpaTestSupport {

    @Autowired
    MessageRoomIdempotencyRepository messageRoomIdempotencyRepository;

    @Test
    @DisplayName("멱등키 엔티티를 저장하고 조회한다")
    void saveAndFind() {
        // given
        MessageRoomIdempotency entity = messageRoomIdempotencyRepository.save(
                new MessageRoomIdempotency(1L, 2L, "idempotency-key", 3L));

        // when // then
        assertThat(messageRoomIdempotencyRepository.findById(entity.getId())).isPresent();
    }

    @Test
    @DisplayName("보낸이/받는이/키로 멱등키 엔티티를 조회한다")
    void findBySenderReceiverAndKey() {
        // given
        MessageRoomIdempotency entity = messageRoomIdempotencyRepository.save(
                new MessageRoomIdempotency(10L, 20L, "key-123", 30L));

        // when
        var found = messageRoomIdempotencyRepository.findBySenderIdAndReceiverIdAndIdempotencyKey(
                10L, 20L, "key-123");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getIdempotencyKey()).isEqualTo("key-123");
    }
}
