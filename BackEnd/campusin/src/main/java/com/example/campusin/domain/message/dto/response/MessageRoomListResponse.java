package com.example.campusin.domain.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(name = "쪽지방 목록 조회 응답", description = "쪽지방 id, 상대방 닉네임, 마지막 쪽지 보낸 시간, 마지막 쪽지 내용")

public class MessageRoomListResponse {

    @Schema(name = "쪽지방 id", example = "1")
    private final Long messageRoomId;

    @Schema(name = "상대방 닉네임", example = "nickname")
    private final String interlocutorNickname;

    @Schema(name = "마지막 쪽지 보낸 시간", example = "2023-05-21 00:00:00")
    private final LocalDateTime lastMessageSentTime;

    @Schema(name = "마지막 쪽지 내용", example = "message content")
    private final String lastMessageContent;

    @Builder
    public MessageRoomListResponse(Long messageRoomId, String interlocutorNickname, LocalDateTime lastMessageSentTime, String lastMessageContent) {
        this.messageRoomId = messageRoomId;
        this.interlocutorNickname = interlocutorNickname;
        this.lastMessageSentTime = lastMessageSentTime;
        this.lastMessageContent = lastMessageContent;
    }
}
