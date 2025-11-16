package com.example.campusin.common.exception;

public class MessageReadNotAllowedException extends BusinessException {
    public MessageReadNotAllowedException() {
        super(ErrorCode.MESSAGE_READ_NOT_ALLOWED);
    }
}
