package com.example.campusin.domain.todo.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@ApiModel(value = "Todo 생성 요청", description = "Todo title, Todo completed(T/F)")
public class TodoRequest {

    @NotBlank
    @ApiModelProperty(value = "Todo 제목(or 내용)")
    private String title;
    @ApiModelProperty(value = "Todo completed(상태) / (True or False)")
    private Boolean completed;

}
