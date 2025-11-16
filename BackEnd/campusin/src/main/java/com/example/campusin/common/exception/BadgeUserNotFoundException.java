package com.example.campusin.common.exception;

public class BadgeUserNotFoundException extends BusinessException {
    public BadgeUserNotFoundException() {
        super(ErrorCode.BADGE_USER_NOT_FOUND);
    }
}
