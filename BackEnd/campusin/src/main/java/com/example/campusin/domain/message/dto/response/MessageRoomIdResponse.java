package com.example.campusin.domain.message.dto.response;

import com.example.campusin.domain.message.MessageRoom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "쪽지방 생성 응답", description = "쪽지방 id")
public class MessageRoomIdResponse {

    @Schema(name = "쪽지방 id", example = "1")
    private final Long messageRoomId;
    public MessageRoomIdResponse(MessageRoom messageRoom) {
        this.messageRoomId = messageRoom.getId();
    }
}
