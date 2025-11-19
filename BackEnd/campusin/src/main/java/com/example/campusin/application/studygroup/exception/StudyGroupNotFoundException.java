package com.example.campusin.application.studygroup.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class StudyGroupNotFoundException extends BusinessException {
    public StudyGroupNotFoundException() {
        super(ErrorCode.STUDY_GROUP_NOT_FOUND);
    }
}
