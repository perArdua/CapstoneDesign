package com.example.campusin.domain.todo.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@ApiModel(value = "Todo 수정 요청", description = "Todo title, Todo completed(T/F)")
public class TodoUpdateRequest {

    @NotBlank
    private String title;
    private Boolean completed;

}
