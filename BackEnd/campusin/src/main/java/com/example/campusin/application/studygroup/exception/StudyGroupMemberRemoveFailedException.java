package com.example.campusin.application.studygroup.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

public class StudyGroupMemberRemoveFailedException extends BusinessException {
    public StudyGroupMemberRemoveFailedException() {
        super(ErrorCode.STUDY_GROUP_MEMBER_REMOVE_FAILED);
    }
}
