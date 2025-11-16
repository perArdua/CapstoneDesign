package com.example.campusin.common.exception;

public class MessageSendNotAllowedException extends BusinessException {
    public MessageSendNotAllowedException() {
        super(ErrorCode.MESSAGE_SEND_NOT_ALLOWED);
    }
}
