package com.example.campusin.application.studygroup.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class StudyGroupNotMemberException extends BusinessException {
    public StudyGroupNotMemberException() {
        super(ErrorCode.STUDY_GROUP_NOT_MEMBER);
    }
}
