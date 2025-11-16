package com.example.campusin.common.exception;

public class StudyGroupNotFoundException extends BusinessException {
    public StudyGroupNotFoundException() {
        super(ErrorCode.STUDY_GROUP_NOT_FOUND);
    }
}
