package com.example.campusin.application.todo.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class TodoNotFoundException extends BusinessException {
    public TodoNotFoundException() {
        super(ErrorCode.TODO_NOT_FOUND);
    }
}
