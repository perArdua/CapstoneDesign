package com.example.campusin.application.comment.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class CommentLikeNotFoundException extends BusinessException {
    public CommentLikeNotFoundException() {
        super(ErrorCode.COMMENT_LIKE_NOT_FOUND);
    }
}
