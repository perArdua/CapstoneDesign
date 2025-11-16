package com.example.campusin.common.exception;

public class StudyGroupNotMemberException extends BusinessException {
    public StudyGroupNotMemberException() {
        super(ErrorCode.STUDY_GROUP_NOT_MEMBER);
    }
}
