package com.example.campusin.domain.message.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ApiModel(value = "쪽지 전송 요청", description = "전송할 쪽지 내용")
public class MessageSendRequest {

    @ApiModelProperty(value = "전송할 쪽지 내용", example = "message content")
    @NotBlank(message = "전송할 쪽지 내용은 필수값입니다.")
    private String message;

}