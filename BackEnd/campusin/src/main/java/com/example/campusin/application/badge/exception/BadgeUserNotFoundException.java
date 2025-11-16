package com.example.campusin.application.badge.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class BadgeUserNotFoundException extends BusinessException {
    public BadgeUserNotFoundException() {
        super(ErrorCode.BADGE_USER_NOT_FOUND);
    }
}
