package com.example.campusin.application.post.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class TagNotFoundException extends BusinessException {
    public TagNotFoundException() {
        super(ErrorCode.TAG_NOT_FOUND);
    }
}
