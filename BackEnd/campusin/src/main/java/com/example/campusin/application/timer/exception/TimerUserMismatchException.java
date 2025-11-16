package com.example.campusin.application.timer.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class TimerUserMismatchException extends BusinessException {
    public TimerUserMismatchException() {
        super(ErrorCode.TIMER_USER_MISMATCH);
    }
}
