package com.example.campusin.domain.studygroup.dto.response;

import lombok.Getter;

@Getter
public class StudyGroupIdResponse {
    private Long id;
    public StudyGroupIdResponse(Long id) {
        this.id = id;
    }
}
