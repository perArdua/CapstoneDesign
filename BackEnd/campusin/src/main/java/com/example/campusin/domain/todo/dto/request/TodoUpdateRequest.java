package com.example.campusin.domain.todo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "Todo 수정 요청", description = "Todo title, Todo completed(T/F)")
public class TodoUpdateRequest {

    @NotBlank
    private String title;
    private Boolean completed;

}
