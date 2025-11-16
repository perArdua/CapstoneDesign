package com.example.campusin.common.exception;

public class BadgePostNotFoundException extends BusinessException {
    public BadgePostNotFoundException() {
        super(ErrorCode.BADGE_POST_NOT_FOUND);
    }
}
