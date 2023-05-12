package com.example.campusin.domain.message.dto.response;

import com.example.campusin.domain.message.Message;
import com.example.campusin.domain.user.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MessageResponse {

    private final Boolean isReceived;
    private final LocalDateTime createdAt;
    private final String content;

    public MessageResponse(Message message, User interlocutor) {
        this.isReceived = interlocutor.getUserId() == message.getWriter().getUserId() ? true : false;
        this.createdAt = message.getCreatedAt();
        this.content = message.getContent();
    }
}
