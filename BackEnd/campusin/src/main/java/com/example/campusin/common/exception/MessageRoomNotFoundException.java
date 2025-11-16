package com.example.campusin.common.exception;

public class MessageRoomNotFoundException extends BusinessException {
    public MessageRoomNotFoundException() {
        super(ErrorCode.MESSAGE_ROOM_NOT_FOUND);
    }
}
