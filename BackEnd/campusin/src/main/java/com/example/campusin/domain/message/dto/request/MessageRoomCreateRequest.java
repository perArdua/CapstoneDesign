package com.example.campusin.domain.message.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(name = "쪽지방 생성 요청", description = "쪽지가 시작된 포스트 id, 쪽지를 받을 사용자 id, 쪽지 내용")
public class MessageRoomCreateRequest {

    @Schema(name = "쪽지가 시작된 포스트 id", example = "1")
    @NotNull(message = "쪽지가 시작된 포스트의 id는 필수값입니다.")
    private Long createdFrom;

    @Schema(name = "쪽지를 받을 사용자 id", example = "1")
    @NotNull(message = "쪽지를 받을 사용자의 id는 필수값입니다.")
    private Long receiverId;

    @Schema(name = "첫 쪽지 내용", example = "first message")
    @NotBlank(message = "첫 쪽지 내용은 필수값입니다.")
    private String firstMessage;
}
