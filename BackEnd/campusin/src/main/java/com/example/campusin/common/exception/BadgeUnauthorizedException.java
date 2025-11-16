package com.example.campusin.common.exception;

public class BadgeUnauthorizedException extends BusinessException {
    public BadgeUnauthorizedException() {
        super(ErrorCode.BADGE_UNAUTHORIZED);
    }
}
