package com.example.campusin.application.message;

import com.example.campusin.domain.message.Message;
import com.example.campusin.domain.message.MessageRoom;
import com.example.campusin.domain.message.VisibilityState;
import com.example.campusin.domain.message.dto.MessageRoomsWithLastMessages;
import com.example.campusin.domain.message.dto.request.MessageRoomCreateRequest;
import com.example.campusin.domain.message.dto.request.MessageRoomGetRequest;
import com.example.campusin.domain.message.dto.response.MessageRoomIdResponse;
import com.example.campusin.domain.message.dto.response.MessageRoomListResponse;
import com.example.campusin.domain.message.dto.response.MessageRoomResponse;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.message.MessageRepository;
import com.example.campusin.infra.message.MessageRoomRepository;
import com.example.campusin.infra.post.PostRepository;
import com.example.campusin.infra.user.UserRepository;
import com.sun.jdi.request.InvalidRequestStateException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageRoomService {

    private final MessageRoomRepository messageRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 쪽지방 생성
    @Transactional
    public MessageRoomIdResponse saveMessageRoom(Long userId, MessageRoomCreateRequest request) {
        if(userId == request.getReceiverId()){
            throw new InvalidRequestStateException("INVALID MESSAGE TARGET");
        }
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
        Post post = postRepository.findById(request.getCreatedFrom())
                .orElseThrow(() -> new IllegalArgumentException("POST NOT FOUND"));

        MessageRoom messageRoom = MessageRoom.builder()
                .initialSender(currentUser)
                .initialReceiver(receiver)
                .createdFrom(post)
                .build();

        MessageRoom savedMessageRoom = messageRoomRepository.save(messageRoom);
        Message message = Message.builder()
                .messageRoom(savedMessageRoom)
                .writer(currentUser)
                .content(request.getFirstMessage())
                .build();
        messageRepository.save(message);
        return new MessageRoomIdResponse(savedMessageRoom);
    }

    // 쪽지방 ID 조회

    @Transactional(readOnly = true)
    public Optional<Long> getMessageRoomId(Long userId, Long createdFrom, Long receiverId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
        userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
        postRepository.findById(createdFrom)
                .orElseThrow(() -> new IllegalArgumentException("POST NOT FOUND"));

        return messageRoomRepository.findIdByInfo(createdFrom, userId, receiverId);
    }

    // 쪽지방 조회

    @Transactional(readOnly = true)
    public MessageRoomResponse getMessageRoom(Long userId, MessageRoomGetRequest request) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
        MessageRoom messageRoom = messageRoomRepository.findById(request.getMessageRoomId())
                .orElseThrow(() -> new IllegalArgumentException("MESSAGE ROOM NOT FOUND"));
        checkMessageRoomIsDeleted(messageRoom, userId);

        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        Page<Message> messages = messageRoomRepository.findMessagesByMessageRoomId(
                messageRoom.getId(), pageable);
        User interlocutor = currentUser.getUserId() == messageRoom.getInitialSender().getUserId()
                ? messageRoom.getInitialReceiver() : messageRoom.getInitialSender();
        return MessageRoomResponse.builder()
                .messages(messages)
                .messageRoom(messageRoom)
                .interlocutor(interlocutor)
                .build();
    }

    // 쪽지방 리스트 조회

    @Transactional(readOnly = true)
    public Page<MessageRoomListResponse> getMessageRooms(Long userId, Pageable pageable) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));

        Page<MessageRoomsWithLastMessages> messageRooms = messageRoomRepository.findMessageRoomsAndLastMessagesByUserId(
                currentUser.getUserSeq(), pageable);

        Page<MessageRoomListResponse> responses = messageRooms.map(messageRoom -> {
            Long interlocutorId = userId == messageRoom.getInitialReceiverId().longValue() ?
                    messageRoom.getInitialSenderId().longValue()
                    : messageRoom.getInitialReceiverId().longValue();
            User interlocutor = userRepository.findById(interlocutorId)
                    .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));

            return MessageRoomListResponse.builder()
                    .messageRoomId(messageRoom.getMessageRoomId().longValue())
                    .interlocutorNickname(interlocutor.getUsername())
                    .lastMessageSentTime(messageRoom.getCreatedAt().toLocalDateTime())
                    .lastMessageContent(messageRoom.getContent())
                    .build();
        });

        return responses;
    }
    // 쪽지방 삭제
    @Transactional
    public void deleteMessageRoom(Long userId, Long messageRoomId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
        MessageRoom messageRoom = messageRoomRepository.findById(messageRoomId)
                .orElseThrow(() -> new IllegalArgumentException("MESSAGE ROOM NOT FOUND"));
        checkUserAuthority(currentUser, messageRoom);

        VisibilityState visibilityState = isInitialSender(currentUser, messageRoom) ?
                VisibilityState.ONLY_INITIAL_RECEIVER : VisibilityState.ONLY_INITIAL_SENDER;
        messageRoom.changeVisibilityTo(visibilityState);
    }

    // 쪽지방 차단
    @Transactional
    public void blockMessageRoom(Long userId, Long messageRoomId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));

        MessageRoom messageRoom = messageRoomRepository.findById(messageRoomId)
                .orElseThrow(() -> new IllegalArgumentException("MESSAGE ROOM NOT FOUND"));

        checkUserAuthority(currentUser, messageRoom);

        messageRoom.changeIsBlocked(true);
    }


    // 쪽지방 수정(삭제, 차단) 권한 확인
    private void checkUserAuthority(User user, MessageRoom messageRoom) {
        if (!(messageRoom.getInitialSender().getUserId() == user.getUserId()) &&
                !(messageRoom.getInitialReceiver().getUserId() == user.getUserId())) {
            throw new IllegalArgumentException("PERMISSION DENIED EXCEPTION : NO PERMISSION TO SEND MESSAGE");
        }
    }

    // 현재 user가 최초 발신자인지 확인

    private boolean isInitialSender(User user, MessageRoom messageRoom) {
        if (messageRoom.getInitialSender().getUserSeq() == user.getUserSeq()) {
            return true;
        }
        return false;
    }


    // 삭제한 쪽지방인지 확인

    private void checkMessageRoomIsDeleted(MessageRoom messageRoom, Long userId) {
        VisibilityState visibility = messageRoom.getVisibilityTo();
        if (visibility.equals(VisibilityState.NO_ONE) ||
                (messageRoom.getInitialSender().getUserSeq() == userId &&
                        visibility.equals(VisibilityState.ONLY_INITIAL_RECEIVER)) ||
                (messageRoom.getInitialReceiver().getUserSeq() == userId &&
                        visibility.equals(VisibilityState.ONLY_INITIAL_SENDER))) {
            throw new IllegalArgumentException("PERMISSION DENIED EXCEPTION : NO PERMISSION TO SEND MESSAGE");
        }
    }
}
