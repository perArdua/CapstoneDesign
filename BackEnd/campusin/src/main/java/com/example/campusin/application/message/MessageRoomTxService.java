package com.example.campusin.application.message;

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
import com.sun.jdi.request.InvalidRequestStateException;
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
        Optional<MessageRoomIdempotency> existing = messageRoomIdempotencyRepository
                .findBySenderIdAndReceiverIdAndIdempotencyKey(senderId, receiverId, idempotencyKey);

        if (existing.isPresent()) {
            return new MessageRoomIdResponse(existing.get().getMessageRoomId());
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("POST NOT FOUND"));

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
