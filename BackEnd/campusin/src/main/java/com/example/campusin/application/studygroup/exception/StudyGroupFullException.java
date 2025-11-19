package com.example.campusin.application.studygroup.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class StudyGroupFullException extends BusinessException {
    public StudyGroupFullException() {
        super(ErrorCode.STUDY_GROUP_FULL);
    }
}
