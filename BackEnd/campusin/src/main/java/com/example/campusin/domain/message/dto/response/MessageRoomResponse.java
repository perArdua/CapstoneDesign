package com.example.campusin.domain.message.dto.response;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.message.Message;
import com.example.campusin.domain.message.MessageRoom;
import com.example.campusin.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Schema(name = "쪽지방 조회 응답", description = "게시판 이름, 게시글 제목, 상대방 닉네임, 쪽지방 차단 여부, 쪽지 내용")
public class MessageRoomResponse {

    @Schema(name = "게시판 이름", example = "FREE")
    private final BoardType boardName;

    @Schema(name = "게시글 제목", example = "title")
    private final String postTitle;

    @Schema(name = "상대방 닉네임", example = "nickname")
    private final String interlocutorNickname;

    @Schema(name = "쪽지방 차단 여부", example = "true")
    private final Boolean isBlocked;

    @Schema(name = "쪽지 내용", example = "isReceived, createdAt, content의 내용이 page형태로 제공됨.", subTypes = MessageResponse.class)
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
