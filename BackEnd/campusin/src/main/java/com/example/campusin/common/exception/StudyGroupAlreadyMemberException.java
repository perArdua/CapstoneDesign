package com.example.campusin.common.exception;

public class StudyGroupAlreadyMemberException extends BusinessException {
    public StudyGroupAlreadyMemberException() {
        super(ErrorCode.STUDY_GROUP_ALREADY_MEMBER);
    }
}
