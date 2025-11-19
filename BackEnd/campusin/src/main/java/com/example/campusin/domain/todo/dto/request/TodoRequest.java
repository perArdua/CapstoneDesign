package com.example.campusin.domain.todo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "Todo 생성 요청", description = "Todo title, Todo completed(T/F)")
public class TodoRequest {

    @NotBlank
    @Schema(description = "Todo 제목(or 내용)")
    private String title;
    @Schema(description = "Todo completed(상태) / (True or False)")
    private Boolean completed;

}
