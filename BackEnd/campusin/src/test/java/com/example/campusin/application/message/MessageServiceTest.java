package com.example.campusin.application.message;

import com.example.campusin.application.message.exception.MessageReadNotAllowedException;
import com.example.campusin.application.message.exception.MessageRoomNotFoundException;
import com.example.campusin.application.message.exception.MessageSendFailedException;
import com.example.campusin.application.message.exception.MessageSendNotAllowedException;
import com.example.campusin.application.user.exception.UserNotFoundException;
import com.example.campusin.domain.message.Message;
import com.example.campusin.domain.message.MessageRoom;
import com.example.campusin.domain.message.VisibilityState;
import com.example.campusin.domain.message.dto.request.MessageSendRequest;
import com.example.campusin.domain.message.dto.response.MessageResponse;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.message.MessageRepository;
import com.example.campusin.infra.message.MessageRoomRepository;
import com.example.campusin.infra.user.UserRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService")
class MessageServiceTest {

    @Mock
    MessageRepository messageRepository;
    @Mock
    MessageRoomRepository messageRoomRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    MessageService messageService;

    @Nested
    @DisplayName("sendMessage 메서드는")
    class Describe_sendMessage {

        @Nested
        @DisplayName("송신자가 방 참여자이고 차단되지 않은 방이면")
        class Context_when_allowed_and_not_blocked {

            @Test
            @DisplayName("메시지를 저장한다")
            void 메시지를_저장한다() {
                // given
                Long userId = 1L;
                Long messageRoomId = 2L;
                User sender = new User();
                sender.setId(userId);
                sender.setLoginId("login-1");

                User receiver = new User();
                receiver.setId(3L);
                receiver.setLoginId("login-3");

                Post createdFrom = new Post();
                MessageRoom room = MessageRoom.builder()
                        .initialSender(sender)
                        .initialReceiver(receiver)
                        .createdFrom(createdFrom)
                        .build();
                room.changeIsBlocked(false);

                MessageSendRequest request = new MessageSendRequest("hi");
                ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

                when(userRepository.findById(userId)).thenReturn(Optional.of(sender));
                when(messageRoomRepository.findById(messageRoomId)).thenReturn(Optional.of(room));
                when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

                // when
                messageService.sendMessage(userId, messageRoomId, request);

                // then
                verify(messageRepository).save(messageCaptor.capture());
                Message saved = messageCaptor.getValue();
                assertThat(saved.getMessageRoom()).isEqualTo(room);
                assertThat(saved.getWriter()).isEqualTo(sender);
                assertThat(saved.getContent()).isEqualTo("hi");
            }
        }

        @Nested
        @DisplayName("사용자가 없으면")
        class Context_when_user_missing {

            @Test
            @DisplayName("UserNotFoundException을 던진다")
            void 예외를_던진다() {
                // given
                when(userRepository.findById(1L)).thenReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> messageService.sendMessage(1L, 2L, new MessageSendRequest("hi")))
                        .isInstanceOf(UserNotFoundException.class);

                verifyNoInteractions(messageRepository);
            }
        }

        @Nested
        @DisplayName("메시지방이 없으면")
        class Context_when_room_missing {

            @Test
            @DisplayName("MessageRoomNotFoundException을 던진다")
            void 예외를_던진다() {
                // given
                when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
                when(messageRoomRepository.findById(2L)).thenReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> messageService.sendMessage(1L, 2L, new MessageSendRequest("hi")))
                        .isInstanceOf(MessageRoomNotFoundException.class);

                verifyNoInteractions(messageRepository);
            }
        }

        @Nested
        @DisplayName("송신자가 참여자가 아니면")
        class Context_when_not_participant {

            @Test
            @DisplayName("MessageSendNotAllowedException을 던진다")
            void 예외를_던진다() {
                // given
                User requester = new User();
                requester.setId(1L);
                requester.setLoginId("x");

                User sender = new User();
                sender.setLoginId("s");
                User receiver = new User();
                receiver.setLoginId("r");

                Post createdFrom = new Post();
                MessageRoom room = MessageRoom.builder()
                        .initialSender(sender)
                        .initialReceiver(receiver)
                        .createdFrom(createdFrom)
                        .build();

                when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
                when(messageRoomRepository.findById(2L)).thenReturn(Optional.of(room));

                // when // then
                assertThatThrownBy(() -> messageService.sendMessage(1L, 2L, new MessageSendRequest("hi")))
                        .isInstanceOf(MessageSendNotAllowedException.class);

                verifyNoInteractions(messageRepository);
            }
        }

        @Nested
        @DisplayName("메시지방이 차단 상태면")
        class Context_when_blocked {

            @Test
            @DisplayName("MessageSendFailedException을 던진다")
            void 예외를_던진다() {
                // given
                User sender = new User();
                sender.setId(1L);
                sender.setLoginId("s");

                User receiver = new User();
                receiver.setId(2L);
                receiver.setLoginId("r");

                Post createdFrom = new Post();
                MessageRoom room = MessageRoom.builder()
                        .initialSender(sender)
                        .initialReceiver(receiver)
                        .createdFrom(createdFrom)
                        .build();
                room.changeIsBlocked(true);

                when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
                when(messageRoomRepository.findById(5L)).thenReturn(Optional.of(room));

                // when // then
                assertThatThrownBy(() -> messageService.sendMessage(1L, 5L, new MessageSendRequest("hi")))
                        .isInstanceOf(MessageSendFailedException.class);

                verifyNoInteractions(messageRepository);
            }
        }
    }

    @Nested
    @DisplayName("getAllMessages 메서드는")
    class Describe_getAllMessages {

        @Nested
        @DisplayName("사용자와 메시지방이 존재하고 조회가 허용되면")
        class Context_when_allowed {

            @Test
            @DisplayName("메시지 페이지를 MessageResponse로 변환해 반환한다")
            void 메시지_목록을_반환한다() {
                // given
                Long userId = 1L;
                Long roomId = 2L;
                User current = new User();
                current.setId(userId);
                current.setLoginId("login-1");
                User other = new User();
                other.setId(3L);
                other.setLoginId("login-3");

                Post createdFrom = new Post();
                MessageRoom room = MessageRoom.builder()
                        .initialSender(current)
                        .initialReceiver(other)
                        .createdFrom(createdFrom)
                        .build();
                room.changeVisibilityTo(VisibilityState.BOTH);

                Message message = Message.builder()
                        .messageRoom(room)
                        .writer(current)
                        .content("hello")
                        .build();

                PageRequest pageable = PageRequest.of(0, 5);
                when(userRepository.findById(userId)).thenReturn(Optional.of(current));
                when(messageRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
                when(messageRoomRepository.findMessagesByMessageRoomId(roomId, pageable))
                        .thenReturn(new PageImpl<>(List.of(message), pageable, 1));

                // when
                Page<MessageResponse> responses = messageService.getAllMessages(userId, roomId, pageable);

                // then
                assertThat(responses.getTotalElements()).isEqualTo(1);
                MessageResponse response = responses.getContent().get(0);
                assertThat(response.getContent()).isEqualTo("hello");
                assertThat(response.getIsReceived()).isFalse();
                verify(messageRoomRepository).findMessagesByMessageRoomId(roomId, pageable);
            }
        }

        @Nested
        @DisplayName("사용자가 없으면")
        class Context_when_user_missing {

            @Test
            @DisplayName("UserNotFoundException을 던진다")
            void 예외를_던진다() {
                // given
                when(userRepository.findById(1L)).thenReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> messageService.getAllMessages(1L, 2L, PageRequest.of(0, 1)))
                        .isInstanceOf(UserNotFoundException.class);

                verifyNoInteractions(messageRoomRepository, messageRepository);
            }
        }

        @Nested
        @DisplayName("메시지방이 없으면")
        class Context_when_room_missing {

            @Test
            @DisplayName("MessageRoomNotFoundException을 던진다")
            void 예외를_던진다() {
                // given
                when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
                when(messageRoomRepository.findById(2L)).thenReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> messageService.getAllMessages(1L, 2L, PageRequest.of(0, 1)))
                        .isInstanceOf(MessageRoomNotFoundException.class);

                verify(messageRoomRepository, never()).findMessagesByMessageRoomId(any(), any());
                verifyNoInteractions(messageRepository);
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

                Post createdFrom = new Post();
                MessageRoom room = MessageRoom.builder()
                        .initialSender(current)
                        .initialReceiver(other)
                        .createdFrom(createdFrom)
                        .build();
                room.changeVisibilityTo(VisibilityState.ONLY_INITIAL_RECEIVER);

                when(userRepository.findById(1L)).thenReturn(Optional.of(current));
                when(messageRoomRepository.findById(3L)).thenReturn(Optional.of(room));

                // when // then
                assertThatThrownBy(() -> messageService.getAllMessages(1L, 3L, PageRequest.of(0, 1)))
                        .isInstanceOf(MessageReadNotAllowedException.class);

                verify(messageRoomRepository, never()).findMessagesByMessageRoomId(any(), any());
            }
        }
    }
}
