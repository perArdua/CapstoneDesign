package com.example.campusin.common.exception;

public class StudyGroupMemberRemoveFailedException extends BusinessException {
    public StudyGroupMemberRemoveFailedException() {
        super(ErrorCode.STUDY_GROUP_MEMBER_REMOVE_FAILED);
    }
}
