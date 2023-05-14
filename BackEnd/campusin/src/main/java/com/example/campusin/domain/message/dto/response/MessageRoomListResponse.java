package com.example.campusin.domain.message.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MessageRoomListResponse {
    private final Long messageRoomId;
    private final String interlocutorNickname;
    private final LocalDateTime lastMessageSentTime;
    private final String lastMessageContent;

    @Builder

    public MessageRoomListResponse(Long messageRoomId, String interlocutorNickname, LocalDateTime lastMessageSentTime, String lastMessageContent) {
        this.messageRoomId = messageRoomId;
        this.interlocutorNickname = interlocutorNickname;
        this.lastMessageSentTime = lastMessageSentTime;
        this.lastMessageContent = lastMessageContent;
    }
}
