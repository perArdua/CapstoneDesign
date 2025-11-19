package com.example.campusin.application.badge.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class BadgePostNotFoundException extends BusinessException {
    public BadgePostNotFoundException() {
        super(ErrorCode.BADGE_POST_NOT_FOUND);
    }
}
