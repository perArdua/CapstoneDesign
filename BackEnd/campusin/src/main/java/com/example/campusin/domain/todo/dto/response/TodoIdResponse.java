package com.example.campusin.domain.todo.dto.response;

import lombok.Getter;

@Getter
public class TodoIdResponse {
    private Long id;

    public TodoIdResponse(Long id) {
        this.id = id;
    }
}
