package com.example.campusin.common.exception;

public class CommentReportNotFoundException extends BusinessException {
    public CommentReportNotFoundException() {
        super(ErrorCode.COMMENT_REPORT_NOT_FOUND);
    }
}
