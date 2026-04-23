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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageRoomTxService {

    private final MessageRoomRepository messageRoomRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MessageRepository messageRepository;
    private final MessageRoomIdempotencyRepository messageRoomIdempotencyRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public MessageRoomIdResponse saveUnderLock(Long senderId, Long receiverId, Long postId, MessageRoomCreateRequest request, String idempotencyKey) {
        // 바깥에서 선행 조회를 통과한 요청이 락 대기 후 진입했을 때,
        // 이미 다른 요청이 생성을 완료했는지 재확인 — 경쟁 진입 방어
        Optional<MessageRoomIdempotency> existing = messageRoomIdempotencyRepository
                .findBySenderIdAndReceiverIdAndIdempotencyKey(senderId, receiverId, idempotencyKey);

        if (existing.isPresent()) {
            return new MessageRoomIdResponse(existing.get().getMessageRoomId());
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(UserNotFoundException::new);
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        MessageRoom room = messageRoomRepository.save(MessageRoom.builder()
                .initialSender(sender)
                .initialReceiver(receiver)
                .createdFrom(post)
                .build());

        messageRepository.save(Message.builder()
                .messageRoom(room)
                .writer(sender)
                .content(request.getFirstMessage())
                .build());

        messageRoomIdempotencyRepository.save(new MessageRoomIdempotency(senderId, receiverId, idempotencyKey, room.getId()));

        return new MessageRoomIdResponse(room);
    }
}
