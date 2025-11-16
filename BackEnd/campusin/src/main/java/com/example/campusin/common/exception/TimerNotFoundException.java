package com.example.campusin.common.exception;

public class TimerNotFoundException extends BusinessException {
    public TimerNotFoundException() {
        super(ErrorCode.TIMER_NOT_FOUND);
    }
}
