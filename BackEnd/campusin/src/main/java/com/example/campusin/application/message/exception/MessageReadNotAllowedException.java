package com.example.campusin.application.message.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class MessageReadNotAllowedException extends BusinessException {
    public MessageReadNotAllowedException() {
        super(ErrorCode.MESSAGE_READ_NOT_ALLOWED);
    }
}
