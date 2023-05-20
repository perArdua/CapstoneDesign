package com.example.campusin.domain.todo.dto.response;

import com.example.campusin.domain.todo.Todo;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TodoResponse {

    private final Long userId;
    private final Long todoId;
    private final String title;
    private final Boolean completed;

    @Builder
    public TodoResponse(Long userId, Long todoId, String title, Boolean completed) {
        this.userId = userId;
        this.todoId = todoId;
        this.title = title;
        this.completed = completed;
    }

    @Builder
    public TodoResponse(Todo entity){
        this(
                entity.getUser().getId(),
                entity.getId(),
                entity.getTitle(),
                entity.getCompleted()
        );
    }
}
