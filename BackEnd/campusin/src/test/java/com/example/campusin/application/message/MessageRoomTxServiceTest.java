package com.example.campusin.application.message;

import com.example.campusin.application.post.exception.PostNotFoundException;
import com.example.campusin.application.user.exception.UserNotFoundException;
import com.example.campusin.domain.message.Message;
import com.example.campusin.domain.message.MessageRoom;
import com.example.campusin.domain.message.MessageRoomIdempotency;
import com.example.campusin.domain.message.dto.request.MessageRoomCreateRequest;
import com.example.campusin.domain.message.dto.response.MessageRoomIdResponse;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.message.MessageRepository;
import com.example.campusin.infra.message.MessageRoomIdempotencyRepository;
import com.example.campusin.infra.message.MessageRoomRepository;
import com.example.campusin.infra.post.PostRepository;
import com.example.campusin.infra.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageRoomTxService")
class MessageRoomTxServiceTest {

    @Mock
    MessageRoomRepository messageRoomRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    MessageRepository messageRepository;
    @Mock
    MessageRoomIdempotencyRepository messageRoomIdempotencyRepository;

    @InjectMocks
    MessageRoomTxService messageRoomTxService;

    @Nested
    @DisplayName("saveUnderLock 메서드는")
    class Describe_saveUnderLock {

        @Nested
        @DisplayName("중복 키가 존재하면")
        class Context_when_idempotent_exists {

            @Test
            @DisplayName("기존 메시지방 ID를 반환하고 추가 저장을 하지 않는다")
            void 기존_ID를_반환한다() {
                // given
                String idempotencyKey = "dup-key";
                MessageRoomIdempotency existing = new MessageRoomIdempotency(1L, 2L, idempotencyKey, 10L);
                when(messageRoomIdempotencyRepository.findBySenderIdAndReceiverIdAndIdempotencyKey(1L, 2L, idempotencyKey))
                        .thenReturn(Optional.of(existing));

                // when
                MessageRoomIdResponse response = messageRoomTxService.saveUnderLock(1L, 2L, 3L,
                        new MessageRoomCreateRequest(3L, 2L, "hello"), idempotencyKey);

                // then
                assertThat(response.getMessageRoomId()).isEqualTo(10L);
                verifyNoInteractions(userRepository, postRepository, messageRoomRepository, messageRepository);
            }
        }

        @Nested
        @DisplayName("새 요청이면")
        class Context_when_new_request {

            @Test
            @DisplayName("사용자/포스트를 조회해 메시지방, 첫 메시지, idempotency를 저장하고 ID를 반환한다")
            void 새_메시지방을_만든다() {
                // given
                Long senderId = 1L;
                Long receiverId = 2L;
                Long postId = 3L;
                String idempotencyKey = "key";

                User sender = new User();
                sender.setId(senderId);
                sender.setLoginId("sender");
                User receiver = new User();
                receiver.setId(receiverId);
                receiver.setLoginId("receiver");
                Post post = new Post();
                ReflectionTestUtils.setField(post, "id", postId);

                MessageRoom savedRoom = MessageRoom.builder()
                        .initialSender(sender)
                        .initialReceiver(receiver)
                        .createdFrom(post)
                        .build();
                ReflectionTestUtils.setField(savedRoom, "id", 11L);

                when(messageRoomIdempotencyRepository.findBySenderIdAndReceiverIdAndIdempotencyKey(senderId, receiverId, idempotencyKey))
                        .thenReturn(Optional.empty());
                when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
                when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
                when(postRepository.findById(postId)).thenReturn(Optional.of(post));
                when(messageRoomRepository.save(any(MessageRoom.class))).thenReturn(savedRoom);
                when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

                MessageRoomCreateRequest request = new MessageRoomCreateRequest(postId, receiverId, "first");

                // when
                MessageRoomIdResponse response = messageRoomTxService.saveUnderLock(senderId, receiverId, postId, request, idempotencyKey);

                // then
                assertThat(response.getMessageRoomId()).isEqualTo(11L);
                ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
                verify(messageRepository).save(messageCaptor.capture());
                Message firstMessage = messageCaptor.getValue();
                assertThat(firstMessage.getMessageRoom()).isEqualTo(savedRoom);
                assertThat(firstMessage.getWriter()).isEqualTo(sender);
                assertThat(firstMessage.getContent()).isEqualTo("first");
                verify(messageRoomIdempotencyRepository).save(any(MessageRoomIdempotency.class));
            }
        }

        @Nested
        @DisplayName("사용자나 포스트가 없으면")
        class Context_when_user_or_post_missing {

            @Test
            @DisplayName("UserNotFoundException을 던진다")
            void 사용자_없음() {
                // given
                when(messageRoomIdempotencyRepository.findBySenderIdAndReceiverIdAndIdempotencyKey(any(), any(), any()))
                        .thenReturn(Optional.empty());
                when(userRepository.findById(1L)).thenReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> messageRoomTxService.saveUnderLock(1L, 2L, 3L,
                        new MessageRoomCreateRequest(3L, 2L, "hi"), "k"))
                        .isInstanceOf(UserNotFoundException.class);

                verifyNoInteractions(postRepository, messageRoomRepository, messageRepository);
            }

            @Test
            @DisplayName("PostNotFoundException을 던진다")
            void 포스트_없음() {
                // given
                User sender = new User();
                sender.setId(1L);
                User receiver = new User();
                receiver.setId(2L);

                when(messageRoomIdempotencyRepository.findBySenderIdAndReceiverIdAndIdempotencyKey(any(), any(), any()))
                        .thenReturn(Optional.empty());
                when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
                when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
                when(postRepository.findById(3L)).thenReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> messageRoomTxService.saveUnderLock(1L, 2L, 3L,
                        new MessageRoomCreateRequest(3L, 2L, "hi"), "k"))
                        .isInstanceOf(PostNotFoundException.class);

                verifyNoInteractions(messageRoomRepository, messageRepository);
            }
        }
    }
}
