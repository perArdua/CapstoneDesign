package com.example.campusin.common.exception;

public class TagNotFoundException extends BusinessException {
    public TagNotFoundException() {
        super(ErrorCode.TAG_NOT_FOUND);
    }
}
