package com.example.campusin.infra.message;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.message.Message;
import com.example.campusin.domain.message.MessageRoom;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MessageRepository")
class MessageRepositoryTest extends DataJpaTestSupport {

    @Autowired
    MessageRepository messageRepository;

    @Test
    @DisplayName("메시지를 저장하고 조회한다")
    void saveAndFind() {
        // given
        User sender = TestEntityFactory.persistUser(em, "msg-sender");
        User receiver = TestEntityFactory.persistUser(em, "msg-receiver");
        Post post = TestEntityFactory.persistPost(em, "message-related", sender, TestEntityFactory.persistBoard(em, BoardType.Free), null);
        MessageRoom room = TestEntityFactory.persistMessageRoom(em, sender, receiver, post);

        Message message = messageRepository.save(Message.builder()
                .content("hello")
                .messageRoom(room)
                .writer(sender)
                .build());

        // when // then
        assertThat(messageRepository.findById(message.getId())).isPresent();
    }
}
