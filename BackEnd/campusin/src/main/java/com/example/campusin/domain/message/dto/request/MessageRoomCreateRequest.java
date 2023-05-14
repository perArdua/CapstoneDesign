package com.example.campusin.domain.message.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MessageRoomCreateRequest {
    @NotNull(message = "쪽지가 시작된 포스트의 id는 필수값입니다.")
    private Long createdFrom;
    @NotNull(message = "쪽지를 받을 사용자의 id는 필수값입니다.")
    private Long receiverId;
    @NotBlank(message = "첫 쪽지 내용은 필수값입니다.")
    private String firstMessage;
}
