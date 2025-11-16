package com.example.campusin.common.exception;

public class TimerUserMismatchException extends BusinessException {
    public TimerUserMismatchException() {
        super(ErrorCode.TIMER_USER_MISMATCH);
    }
}
