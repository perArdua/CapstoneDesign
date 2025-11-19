package com.example.campusin.application.studygroup.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class StudyGroupAlreadyMemberException extends BusinessException {
    public StudyGroupAlreadyMemberException() {
        super(ErrorCode.STUDY_GROUP_ALREADY_MEMBER);
    }
}
