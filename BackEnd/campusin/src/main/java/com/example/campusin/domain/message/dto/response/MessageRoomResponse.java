package com.example.campusin.domain.message.dto.response;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.message.Message;
import com.example.campusin.domain.message.MessageRoom;
import com.example.campusin.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class MessageRoomResponse {

    private final BoardType boardName;
    private final String postTitle;
    private final String interlocutorNickname;
    private final Boolean isBlocked;
    private final Page<MessageResponse> message;

    @Builder

    public MessageRoomResponse(MessageRoom messageRoom, Page<Message> messages, User interlocutor) {
        this.boardName = messageRoom.getCreatedFrom().getBoard().getBoardType();
        this.postTitle = messageRoom.getCreatedFrom().getTitle();
        this.interlocutorNickname = interlocutor.getUsername();
        this.isBlocked = messageRoom.getIsBlocked();
        this.message = messages.map(message -> new MessageResponse(message, interlocutor));;
    }
}
