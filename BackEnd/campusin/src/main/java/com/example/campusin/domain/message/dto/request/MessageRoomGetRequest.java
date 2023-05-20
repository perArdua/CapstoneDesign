package com.example.campusin.domain.message.dto.request;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(name = "쪽지방 조회 요청", description = "쪽지방 id 입력")

public class MessageRoomGetRequest {

    @Schema(name = "쪽지방 id", example = "1")
    @NotNull(message = "쪽지방 id는 필수값입니다.")
    private Long messageRoomId;

}