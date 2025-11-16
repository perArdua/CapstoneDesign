package com.example.campusin.application.message.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class MessageSendNotAllowedException extends BusinessException {
    public MessageSendNotAllowedException() {
        super(ErrorCode.MESSAGE_SEND_NOT_ALLOWED);
    }
}
