package com.example.campusin.application.message.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class MessageSendFailedException extends BusinessException {
    public MessageSendFailedException() {
        super(ErrorCode.MESSAGE_SEND_FAILED);
    }
}
