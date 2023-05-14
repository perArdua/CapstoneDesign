package com.example.campusin.domain.message.dto.response;

import com.example.campusin.domain.message.MessageRoom;
import lombok.Getter;

@Getter
public class MessageRoomIdResponse {
    private final Long messageRoomId;

    public MessageRoomIdResponse(MessageRoom messageRoom) {
        this.messageRoomId = messageRoom.getId();
    }
}
