package com.example.campusin.domain.message.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ApiModel(value = "쪽지방 생성 요청", description = "쪽지가 시작된 포스트 id, 쪽지를 받을 사용자 id, 쪽지 내용")
public class MessageRoomCreateRequest {
    @ApiModelProperty(value = "쪽지가 시작된 Post id")
    @NotNull(message = "쪽지가 시작된 포스트의 id는 필수값입니다.")
    private Long createdFrom;
    @NotNull(message = "쪽지를 받을 사용자의 id는 필수값입니다.")
    @ApiModelProperty(value = "쪽지를 받을 사용자의 id")
    private Long receiverId;
    @NotBlank(message = "첫 쪽지 내용은 필수값입니다.")
    @ApiModelProperty(value = "첫 쪽지 내용", example = "first message")
    private String firstMessage;
}
