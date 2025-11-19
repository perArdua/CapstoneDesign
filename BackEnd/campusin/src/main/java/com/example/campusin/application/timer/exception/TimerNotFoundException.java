package com.example.campusin.application.timer.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class TimerNotFoundException extends BusinessException {
    public TimerNotFoundException() {
        super(ErrorCode.TIMER_NOT_FOUND);
    }
}
