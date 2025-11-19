package com.example.campusin.application.comment.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class CommentReportNotFoundException extends BusinessException {
    public CommentReportNotFoundException() {
        super(ErrorCode.COMMENT_REPORT_NOT_FOUND);
    }
}
