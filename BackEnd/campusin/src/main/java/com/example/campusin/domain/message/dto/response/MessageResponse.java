package com.example.campusin.domain.message.dto.response;

import com.example.campusin.domain.message.Message;
import com.example.campusin.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(name = "쪽지 조회 응답", description = "쪽지를 받은 사람인지, 쪽지 생성 시간, 쪽지 내용")
public class MessageResponse {

    @Schema(name = "쪽지를 받은 사람인지", example = "true")
    private final Boolean isReceived;

    @Schema(name = "쪽지 생성 시간", example = "2023-05-21 00:00:00")
    private final LocalDateTime createdAt;

    @Schema(name = "쪽지 내용", example = "message content")
    private final String content;

    public MessageResponse(Message message, User interlocutor) {
        this.isReceived = interlocutor.getLoginId() == message.getWriter().getLoginId() ? true : false;
        this.createdAt = message.getCreatedAt();
        this.content = message.getContent();
    }
}
