package com.example.campusin.common.exception;

public class StudyGroupFullException extends BusinessException {
    public StudyGroupFullException() {
        super(ErrorCode.STUDY_GROUP_FULL);
    }
}
