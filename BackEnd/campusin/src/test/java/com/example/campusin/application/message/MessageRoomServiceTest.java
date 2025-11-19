package com.example.campusin.application.message;

import com.example.campusin.application.message.exception.MessageReadNotAllowedException;
import com.example.campusin.application.message.exception.MessageRoomNotFoundException;
import com.example.campusin.application.message.exception.MessageSendNotAllowedException;
import com.example.campusin.application.post.exception.PostNotFoundException;
import com.example.campusin.application.user.exception.UserNotFoundException;
import com.example.campusin.domain.message.Message;
import com.example.campusin.domain.message.MessageRoom;
import com.example.campusin.domain.message.VisibilityState;
import com.example.campusin.domain.message.dto.MessageRoomsWithLastMessages;
import com.example.campusin.domain.message.dto.request.MessageRoomCreateRequest;
import com.example.campusin.domain.message.dto.request.MessageRoomGetRequest;
import com.example.campusin.domain.message.dto.response.MessageRoomIdResponse;
import com.example.campusin.domain.message.dto.response.MessageRoomListResponse;
import com.example.campusin.domain.message.dto.response.MessageRoomResponse;
import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.message.MessageRepository;
import com.example.campusin.infra.message.MessageRoomRepository;
import com.example.campusin.infra.post.PostRepository;
import com.example.campusin.infra.user.UserRepository;
import com.sun.jdi.request.InvalidRequestStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageRoomService")
class MessageRoomServiceTest {

    @Mock
    MessageRoomRepository messageRoomRepository;
    @Mock
    MessageRepository messageRepository;
    @Mock
    MessageRoomTxService messageRoomTxService;
    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;

    @InjectMocks
    MessageRoomService messageRoomService;

    private Post newPostWithId(Long id) {
        Board board = Board.builder().boardType(BoardType.Free).build();
        Post post = Post.builder()
                .title("title-" + id)
                .content("content")
                .board(board)
                .user(new User())
                .price(0L)
                .studyGroupId(-1L)
                .build();
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }

    private MessageRoom newRoom(User sender, User receiver, Post post, Long id) {
        MessageRoom room = MessageRoom.builder()
                .initialSender(sender)
                .initialReceiver(receiver)
                .createdFrom(post)
                .build();
        ReflectionTestUtils.setField(room, "id", id);
        return room;
    }

    @Nested
    @DisplayName("saveMessageRoom 메서드는")
    class Describe_saveMessageRoom {

        @Nested
        @DisplayName("송신자와 수신자가 다른 경우")
        class Context_when_valid_target {

            @Test
            @DisplayName("락을 획득하고 Tx 서비스로 위임해 ID를 반환한다")
            void 락을_획득하고_위임한다() {
                // given
                Long userId = 1L;
                MessageRoomCreateRequest request = new MessageRoomCreateRequest(3L, 2L, "first");
                String idempotencyKey = "k";
                String lockName = "msgRoom:create:3:1:2";

                when(messageRoomRepository.acquireLock(lockName, 30)).thenReturn(1);
                when(messageRoomTxService.saveUnderLock(userId, 2L, 3L, request, idempotencyKey))
                        .thenReturn(new MessageRoomIdResponse(99L));

                // when
                MessageRoomIdResponse response = messageRoomService.saveMessageRoom(userId, request, idempotencyKey);

                // then
                assertThat(response.getMessageRoomId()).isEqualTo(99L);
                verify(messageRoomRepository).releaseLock(lockName);
            }

            @Test
            @DisplayName("락 획득 후 예외가 발생해도 락을 해제한다")
            void 예외시_락을_해제한다() {
                // given
                Long userId = 1L;
                MessageRoomCreateRequest request = new MessageRoomCreateRequest(3L, 2L, "first");
                String lockName = "msgRoom:create:3:1:2";

                when(messageRoomRepository.acquireLock(lockName, 30)).thenReturn(1);
                doThrow(new IllegalStateException("tx fail"))
                        .when(messageRoomTxService).saveUnderLock(userId, 2L, 3L, request, "k");

                // when // then
                assertThatThrownBy(() -> messageRoomService.saveMessageRoom(userId, request, "k"))
                        .isInstanceOf(IllegalStateException.class);
                verify(messageRoomRepository).releaseLock(lockName);
            }
        }

        @Nested
        @DisplayName("송신자와 수신자가 같으면")
        class Context_when_same_user {

            @Test
            @DisplayName("InvalidRequestStateException을 던진다")
            void 예외를_던진다() {
                // given
                MessageRoomCreateRequest request = new MessageRoomCreateRequest(1L, 1L, "first");

                // when // then
                assertThatThrownBy(() -> messageRoomService.saveMessageRoom(1L, request, "k"))
                        .isInstanceOf(InvalidRequestStateException.class);

                verifyNoInteractions(messageRoomTxService, messageRoomRepository);
            }
        }

        @Nested
        @DisplayName("락 획득에 실패하면")
        class Context_when_lock_failed {

            @Test
            @DisplayName("IllegalStateException을 던진다")
            void 예외를_던진다() {
                // given
                MessageRoomCreateRequest request = new MessageRoomCreateRequest(3L, 2L, "first");
                when(messageRoomRepository.acquireLock("msgRoom:create:3:1:2", 30)).thenReturn(0);

                // when // then
                assertThatThrownBy(() -> messageRoomService.saveMessageRoom(1L, request, "k"))
                        .isInstanceOf(IllegalStateException.class);
            }
        }
    }

    @Nested
    @DisplayName("getMessageRoomId 메서드는")
    class Describe_getMessageRoomId {

        @Nested
        @DisplayName("사용자와 포스트가 존재하면")
        class Context_when_entities_exist {

            @Test
            @DisplayName("메시지방 ID Optional을 반환한다")
            void 아이디를_반환한다() {
                // given
                when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
                when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
                when(postRepository.findById(3L)).thenReturn(Optional.of(newPostWithId(3L)));
                when(messageRoomRepository.findIdByInfo(3L, 1L, 2L)).thenReturn(Optional.of(7L));

                // when
                Optional<Long> result = messageRoomService.getMessageRoomId(1L, 3L, 2L);

                // then
                assertThat(result).contains(7L);
            }
        }

        @Nested
        @DisplayName("사용자나 포스트가 없으면")
        class Context_when_missing_entities {

            @Test
            @DisplayName("UserNotFoundException 또는 PostNotFoundException을 던진다")
            void 예외를_던진다() {
                // given
                when(userRepository.findById(1L)).thenReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> messageRoomService.getMessageRoomId(1L, 3L, 2L))
                        .isInstanceOf(UserNotFoundException.class);
                verifyNoInteractions(messageRoomRepository);
            }
        }
    }

    @Nested
    @DisplayName("getMessageRoom 메서드는")
    class Describe_getMessageRoom {

        @Nested
        @DisplayName("조회 허용된 경우")
        class Context_when_allowed {

            @Test
            @DisplayName("MessageRoomResponse를 반환한다")
            void 응답을_반환한다() {
                // given
                Long userId = 1L;
                User current = new User();
                current.setId(userId);
                current.setLoginId("login");
                User other = new User();
                other.setId(2L);
                other.setLoginId("other");

                MessageRoom room = newRoom(current, other, newPostWithId(3L), 4L);
                room.changeVisibilityTo(VisibilityState.BOTH);

                Message message = Message.builder()
                        .messageRoom(room)
                        .writer(current)
                        .content("hello")
                        .build();
                when(userRepository.findById(userId)).thenReturn(Optional.of(current));
                when(messageRoomRepository.findById(4L)).thenReturn(Optional.of(room));
                when(messageRoomRepository.findMessagesByMessageRoomId(eq(4L), any(Pageable.class)))
                        .thenReturn(new PageImpl<>(List.of(message)));

                // when
                MessageRoomResponse response = messageRoomService.getMessageRoom(userId, new MessageRoomGetRequest(4L));

                // then
                assertThat(response.getPostTitle()).isEqualTo("title-3");
                assertThat(response.getBoardName()).isEqualTo(BoardType.Free);
                assertThat(response.getInterlocutorNickname()).isEqualTo(other.getUsername());
                assertThat(response.getMessage().getTotalElements()).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("삭제된 방이면")
        class Context_when_deleted {

            @Test
            @DisplayName("MessageReadNotAllowedException을 던진다")
            void 예외를_던진다() {
                // given
                User current = new User();
                current.setId(1L);
                current.setLoginId("me");
                User other = new User();
                other.setId(2L);
                other.setLoginId("you");
                MessageRoom room = newRoom(current, other, newPostWithId(3L), 4L);
                room.changeVisibilityTo(VisibilityState.ONLY_INITIAL_RECEIVER);

                when(userRepository.findById(1L)).thenReturn(Optional.of(current));
                when(messageRoomRepository.findById(4L)).thenReturn(Optional.of(room));

                // when // then
                assertThatThrownBy(() -> messageRoomService.getMessageRoom(1L, new MessageRoomGetRequest(4L)))
                        .isInstanceOf(MessageReadNotAllowedException.class);

                verify(messageRoomRepository, never()).findMessagesByMessageRoomId(any(), any());
            }
        }
    }

    @Nested
    @DisplayName("getMessageRooms 메서드는")
    class Describe_getMessageRooms {

        @Test
        @DisplayName("사용자의 대화방 목록을 반환한다")
        void 대화방_목록을_반환한다() {
            // given
            Long userId = 1L;
            User current = new User();
            current.setId(userId);

            MessageRoomsWithLastMessages projection = new MessageRoomsWithLastMessages() {
                @Override
                public BigInteger getMessageRoomId() {
                    return BigInteger.TEN;
                }

                @Override
                public Boolean getIsAnonymous() {
                    return false;
                }

                @Override
                public BigInteger getInitialReceiverId() {
                    return BigInteger.ONE;
                }

                @Override
                public BigInteger getInitialSenderId() {
                    return BigInteger.valueOf(2L);
                }

                @Override
                public Timestamp getCreatedAt() {
                    return Timestamp.valueOf(LocalDateTime.of(2023, 1, 1, 10, 0));
                }

                @Override
                public String getContent() {
                    return "last";
                }
            };

            User interlocutor = new User();
            interlocutor.setId(2L);
            interlocutor.setUsername("상대");

            Pageable pageable = PageRequest.of(0, 10);
            when(userRepository.findById(userId)).thenReturn(Optional.of(current));
            when(messageRoomRepository.findMessageRoomsAndLastMessagesByUserId(userId, pageable))
                    .thenReturn(new PageImpl<>(List.of(projection), pageable, 1));
            when(userRepository.findById(2L)).thenReturn(Optional.of(interlocutor));

            // when
            Page<MessageRoomListResponse> responses = messageRoomService.getMessageRooms(userId, pageable);

            // then
            assertThat(responses.getTotalElements()).isEqualTo(1);
            MessageRoomListResponse response = responses.getContent().get(0);
            assertThat(response.getMessageRoomId()).isEqualTo(10L);
            assertThat(response.getInterlocutorNickname()).isEqualTo("상대");
            assertThat(response.getLastMessageContent()).isEqualTo("last");
        }
    }

    @Nested
    @DisplayName("deleteMessageRoom 메서드는")
    class Describe_deleteMessageRoom {

        @Nested
        @DisplayName("권한이 있으면")
        class Context_when_authorized {

            @Test
            @DisplayName("visibility를 상대만 보도록 변경한다")
            void visibility를_변경한다() {
                // given
                User current = new User();
                current.setId(1L);
                current.setLoginId("same");
                User other = new User();
                other.setId(2L);
                other.setLoginId("same"); // == 비교 통과를 위해 같은 리터럴

                MessageRoom room = newRoom(current, other, newPostWithId(1L), 5L);
                room.changeVisibilityTo(VisibilityState.BOTH);

                when(userRepository.findById(1L)).thenReturn(Optional.of(current));
                when(messageRoomRepository.findById(5L)).thenReturn(Optional.of(room));

                // when
                messageRoomService.deleteMessageRoom(1L, 5L);

                // then
                assertThat(room.getVisibilityTo()).isEqualTo(VisibilityState.ONLY_INITIAL_RECEIVER);
            }
        }

        @Nested
        @DisplayName("권한이 없으면")
        class Context_when_not_authorized {

            @Test
            @DisplayName("MessageSendNotAllowedException을 던진다")
            void 예외를_던진다() {
                // given
                User current = new User();
                current.setLoginId("me");
                User other = new User();
                other.setLoginId("you");
                MessageRoom room = newRoom(other, new User(), newPostWithId(1L), 5L);

                when(userRepository.findById(1L)).thenReturn(Optional.of(current));
                when(messageRoomRepository.findById(5L)).thenReturn(Optional.of(room));

                // when // then
                assertThatThrownBy(() -> messageRoomService.deleteMessageRoom(1L, 5L))
                        .isInstanceOf(MessageSendNotAllowedException.class);
            }
        }
    }

    @Nested
    @DisplayName("blockMessageRoom 메서드는")
    class Describe_blockMessageRoom {

        @Nested
        @DisplayName("권한이 있으면")
        class Context_when_authorized {

            @Test
            @DisplayName("쪽지방을 차단한다")
            void 차단한다() {
                // given
                User current = new User();
                current.setLoginId("same");
                current.setId(1L);
                User other = new User();
                other.setLoginId("same");
                other.setId(2L);
                MessageRoom room = newRoom(current, other, newPostWithId(1L), 6L);
                room.changeIsBlocked(false);

                when(userRepository.findById(1L)).thenReturn(Optional.of(current));
                when(messageRoomRepository.findById(6L)).thenReturn(Optional.of(room));

                // when
                messageRoomService.blockMessageRoom(1L, 6L);

                // then
                assertThat(room.getIsBlocked()).isTrue();
            }
        }

        @Nested
        @DisplayName("권한이 없으면")
        class Context_when_not_authorized {

            @Test
            @DisplayName("MessageSendNotAllowedException을 던진다")
            void 예외를_던진다() {
                // given
                User current = new User();
                current.setLoginId("me");
                User other = new User();
                other.setLoginId("you");
                MessageRoom room = newRoom(other, new User(), newPostWithId(1L), 7L);

                when(userRepository.findById(1L)).thenReturn(Optional.of(current));
                when(messageRoomRepository.findById(7L)).thenReturn(Optional.of(room));

                // when // then
                assertThatThrownBy(() -> messageRoomService.blockMessageRoom(1L, 7L))
                        .isInstanceOf(MessageSendNotAllowedException.class);
                assertThat(room.getIsBlocked()).isFalse();
            }
        }
    }
}
