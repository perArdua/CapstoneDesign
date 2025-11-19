package com.example.campusin.application.post.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class PostNotFoundException extends BusinessException {
    public PostNotFoundException() {
        super(ErrorCode.POST_NOT_FOUND);
    }
}
