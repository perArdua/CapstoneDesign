package com.example.campusin.domain.message.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(name = "쪽지 전송 요청", description = "전송할 쪽지 내용")
public class MessageSendRequest {

    @Schema(name = "전송할 쪽지 내용", example = "message content")
    @NotBlank(message = "전송할 쪽지 내용은 필수값입니다.")
    private String message;

}