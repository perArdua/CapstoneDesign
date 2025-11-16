package com.example.campusin.common.exception;

public class MessageSendFailedException extends BusinessException {
    public MessageSendFailedException() {
        super(ErrorCode.MESSAGE_SEND_FAILED);
    }
}
