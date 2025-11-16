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
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.message.MessageRepository;
import com.example.campusin.infra.message.MessageRoomRepository;
import com.example.campusin.infra.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageRoomRepository messageRoomRepository;
    private final UserRepository userRepository;

    // 쪽지 전송
    @Transactional
    public void sendMessage(Long userId, Long messageRoomId, MessageSendRequest request) {
        User currentUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        MessageRoom messageRoom = messageRoomRepository.findById(messageRoomId).orElseThrow(MessageRoomNotFoundException::new);
        checkUserAuthority(currentUser, messageRoom);
        checkMessageRoomIsBlocked(messageRoom);

        Message message = Message.builder()
                .messageRoom(messageRoom)
                .writer(currentUser)
                .content(request.getMessage())
                .build();
        messageRepository.save(message);
    }

    // 쪽지 다건 조회

    @Transactional(readOnly = true)
    public Page<MessageResponse> getAllMessages(Long userId, Long messageRoomId, Pageable pageable) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        MessageRoom messageRoom = messageRoomRepository.findById(messageRoomId)
                .orElseThrow(MessageRoomNotFoundException::new);
        checkMessageRoomIsDeleted(messageRoom, userId);

        Page<Message> messages = messageRoomRepository.findMessagesByMessageRoomId(
                messageRoomId, pageable);
        User interlocutor = currentUser.getLoginId() == messageRoom.getInitialSender().getLoginId()
                ? messageRoom.getInitialReceiver() : messageRoom.getInitialSender();
        return messages.map(message -> new MessageResponse(message, interlocutor));
    }

    //쪽지 전송 권한 확인

    private void checkUserAuthority(User user, MessageRoom messageRoom) {
        if (!(messageRoom.getInitialSender().getLoginId() == user.getLoginId()) &&
                !(messageRoom.getInitialReceiver().getLoginId() == user.getLoginId())) {
            throw new MessageSendNotAllowedException();
        }
    }


    // 차단된 쪽지방인지 확인

    private void checkMessageRoomIsBlocked(MessageRoom messageRoom) {
        if (messageRoom.getIsBlocked()) {
            throw new MessageSendFailedException();
        }
    }


    // 삭제한 쪽지방인지 확인

    private void checkMessageRoomIsDeleted(MessageRoom messageRoom, Long userId) {
        VisibilityState visibility = messageRoom.getVisibilityTo();
        if (visibility.equals(VisibilityState.NO_ONE) ||
                (messageRoom.getInitialSender().getId() == userId &&
                        visibility.equals(VisibilityState.ONLY_INITIAL_RECEIVER)) ||
                (messageRoom.getInitialReceiver().getId() == userId &&
                        visibility.equals(VisibilityState.ONLY_INITIAL_SENDER))) {
            throw new MessageReadNotAllowedException();
        }
    }
}
