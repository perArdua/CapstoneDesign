package com.example.campusin.domain.message.dto;

import java.math.BigInteger;
import java.sql.Timestamp;

public interface MessageRoomsWithLastMessages {
    BigInteger getMessageRoomId();
    Boolean getIsAnonymous();
    BigInteger getInitialReceiverId();
    BigInteger getInitialSenderId();
    Timestamp getCreatedAt();

    String getContent();
}