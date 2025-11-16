package com.example.campusin.common.exception;

public class CommentLikeNotFoundException extends BusinessException {
    public CommentLikeNotFoundException() {
        super(ErrorCode.COMMENT_LIKE_NOT_FOUND);
    }
}
