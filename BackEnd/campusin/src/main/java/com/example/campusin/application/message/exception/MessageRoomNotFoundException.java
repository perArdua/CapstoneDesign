package com.example.campusin.application.message.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class MessageRoomNotFoundException extends BusinessException {
    public MessageRoomNotFoundException() {
        super(ErrorCode.MESSAGE_ROOM_NOT_FOUND);
    }
}
