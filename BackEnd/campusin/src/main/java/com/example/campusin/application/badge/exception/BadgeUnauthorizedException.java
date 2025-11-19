package com.example.campusin.application.badge.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class BadgeUnauthorizedException extends BusinessException {
    public BadgeUnauthorizedException() {
        super(ErrorCode.BADGE_UNAUTHORIZED);
    }
}
