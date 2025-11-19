package com.example.campusin.infra.message;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.message.MessageRoom;
import com.example.campusin.domain.message.Message;
import com.example.campusin.domain.message.dto.MessageRoomsWithLastMessages;
import com.example.campusin.domain.tag.TagType;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import com.example.campusin.support.H2LockFunctions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MessageRoomRepository")
class MessageRoomRepositoryTest extends DataJpaTestSupport {

    @Autowired
    MessageRoomRepository messageRoomRepository;

    @Test
    @DisplayName("보낸이, 받는이, 게시글 조합으로 방을 조회하고 존재 여부를 확인한다")
    void 조합으로_조회하고_존재여부를_확인한다() {
        // given
        User sender = TestEntityFactory.persistUser(em, "sender");
        User receiver = TestEntityFactory.persistUser(em, "receiver");
        var board = TestEntityFactory.persistBoard(em, BoardType.Free);
        var tag = TestEntityFactory.persistTag(em, TagType.Science);
        var post = TestEntityFactory.persistPost(em, "message-post", sender, board, tag);
        MessageRoom room = TestEntityFactory.persistMessageRoom(em, sender, receiver, post);

        // when
        MessageRoom foundByInfo = messageRoomRepository.findMessageRoomByInfo(
                post.getId(), sender.getId(), receiver.getId()).orElse(null);
        boolean existsSwapped = messageRoomRepository.existsByInfo(
                post.getId(), receiver.getId(), sender.getId());
        MessageRoom foundByPair = messageRoomRepository.findByUserPairAndPost(
                sender.getId(), receiver.getId(), post.getId()).orElse(null);

        // then
        assertThat(foundByInfo).isNotNull();
        assertThat(foundByInfo.getId()).isEqualTo(room.getId());
        assertThat(existsSwapped).isTrue();
        assertThat(foundByPair).isEqualTo(room);
    }

    @Test
    @DisplayName("findIdByInfo는 방이 없으면 빈 Optional을, 있을 때는 ID를 반환한다")
    void findIdByInfo_존재여부() {
        // given
        User sender = TestEntityFactory.persistUser(em, "sender2");
        User receiver = TestEntityFactory.persistUser(em, "receiver2");
        var board = TestEntityFactory.persistBoard(em, BoardType.Free);
        var tag = TestEntityFactory.persistTag(em, TagType.Art);
        var post = TestEntityFactory.persistPost(em, "post2", sender, board, tag);

        // when
        var emptyResult = messageRoomRepository.findIdByInfo(post.getId(), sender.getId(), receiver.getId());
        MessageRoom room = TestEntityFactory.persistMessageRoom(em, sender, receiver, post);
        var found = messageRoomRepository.findIdByInfo(post.getId(), sender.getId(), receiver.getId());

        // then
        assertThat(emptyResult).isEmpty();
        assertThat(found).contains(room.getId());
    }

    @Test
    @DisplayName("메시지 룸 ID로 메시지를 페이징 조회한다")
    void findMessagesByMessageRoomId() {
        // given
        User sender = TestEntityFactory.persistUser(em, "sender3");
        User receiver = TestEntityFactory.persistUser(em, "receiver3");
        var board = TestEntityFactory.persistBoard(em, BoardType.Free);
        var tag = TestEntityFactory.persistTag(em, TagType.IT);
        var post = TestEntityFactory.persistPost(em, "post3", sender, board, tag);
        MessageRoom room = TestEntityFactory.persistMessageRoom(em, sender, receiver, post);
        Message first = em.persistAndFlush(Message.builder().messageRoom(room).writer(sender).content("first").build());
        Message second = em.persistAndFlush(Message.builder().messageRoom(room).writer(receiver).content("second").build());

        // when
        Page<Message> page = messageRoomRepository.findMessagesByMessageRoomId(room.getId(), PageRequest.of(0, 10));

        // then
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(Message::getContent)
                .containsExactlyInAnyOrder(first.getContent(), second.getContent());
    }

    @Test
    @DisplayName("사용자별 메시지룸과 마지막 메시지를 조회한다")
    void findMessageRoomsAndLastMessagesByUserId() {
        // given
        User alice = TestEntityFactory.persistUser(em, "alice");
        User bob = TestEntityFactory.persistUser(em, "bob");
        User carol = TestEntityFactory.persistUser(em, "carol");
        var board = TestEntityFactory.persistBoard(em, BoardType.Free);
        var tag = TestEntityFactory.persistTag(em, TagType.Science);
        var post1 = TestEntityFactory.persistPost(em, "post-room1", alice, board, tag);
        var post2 = TestEntityFactory.persistPost(em, "post-room2", bob, board, tag);
        MessageRoom room1 = TestEntityFactory.persistMessageRoom(em, alice, bob, post1);
        MessageRoom room2 = TestEntityFactory.persistMessageRoom(em, alice, carol, post2);

        Message oldMsg = Message.builder().messageRoom(room1).writer(bob).content("old").build();
        em.persist(oldMsg);
        Message latestRoom1 = Message.builder().messageRoom(room1).writer(alice).content("latest-1").build();
        em.persist(latestRoom1);

        Message onlyRoom2 = Message.builder().messageRoom(room2).writer(carol).content("only-room2").build();
        em.persist(onlyRoom2);
        em.flush();
        setCreatedAt(oldMsg, LocalDateTime.now().minusMinutes(5));
        setCreatedAt(latestRoom1, LocalDateTime.now());
        setCreatedAt(onlyRoom2, LocalDateTime.now().minusMinutes(1));

        // when
        Page<MessageRoomsWithLastMessages> page =
                messageRoomRepository.findMessageRoomsAndLastMessagesByUserId(alice.getId(), PageRequest.of(0, 10));

        // then
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent())
                .extracting(MessageRoomsWithLastMessages::getContent)
                .containsExactlyInAnyOrder("latest-1", "only-room2");
    }

    @Test
    @DisplayName("보낸이/받는이/게시글 조합으로 생성된 방 개수를 센다")
    void countByInitialSenderAndReceiverAndPost() {
        // given
        User sender = TestEntityFactory.persistUser(em, "count-sender");
        User receiver = TestEntityFactory.persistUser(em, "count-receiver");
        var board = TestEntityFactory.persistBoard(em, BoardType.Free);
        var tag = TestEntityFactory.persistTag(em, TagType.IT);
        var post = TestEntityFactory.persistPost(em, "post-count", sender, board, tag);
        TestEntityFactory.persistMessageRoom(em, sender, receiver, post);

        // when
        long count = messageRoomRepository.countByInitialSenderIdAndInitialReceiverIdAndCreatedFromId(
                sender.getId(), receiver.getId(), post.getId());

        // then
        assertThat(count).isEqualTo(1L);
    }

    @Test
    @DisplayName("DB락 획득/해제를 실행한다")
    void acquireAndReleaseLock() {
        // given
        registerLockFunctions();

        // when
        int acquired = messageRoomRepository.acquireLock("lock-key", 1);
        int released = messageRoomRepository.releaseLock("lock-key");

        // then
        assertThat(acquired).isEqualTo(1);
        assertThat(released).isEqualTo(1);
    }

    private void registerLockFunctions() {
        String className = H2LockFunctions.class.getName();
        em.getEntityManager().createNativeQuery(
                        "CREATE ALIAS IF NOT EXISTS GET_LOCK FOR \"" + className + ".getLock\"")
                .executeUpdate();
        em.getEntityManager().createNativeQuery(
                        "CREATE ALIAS IF NOT EXISTS RELEASE_LOCK FOR \"" + className + ".releaseLock\"")
                .executeUpdate();
    }

    private void setCreatedAt(Message message, LocalDateTime time) {
        em.getEntityManager().createNativeQuery("UPDATE message SET created_at = ? WHERE message_id = ?")
                .setParameter(1, Timestamp.valueOf(time))
                .setParameter(2, message.getId())
                .executeUpdate();
        em.clear();
    }
}
