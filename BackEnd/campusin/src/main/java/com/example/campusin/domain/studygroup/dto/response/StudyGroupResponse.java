package com.example.campusin.domain.studygroup.dto.response;

import com.example.campusin.domain.studygroup.StudyGroup;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StudyGroupResponse {
    private Long id;
    private String studygroupName;
    private int LimitedMemberSize;
    private Long userId;

    private LocalDateTime createdAt;
    @Builder
    public StudyGroupResponse(Long id, String studygroupName, int LimitedMemberSize, Long userId, LocalDateTime createdAt) {
        this.id = id;
        this.studygroupName = studygroupName;
        this.LimitedMemberSize = LimitedMemberSize;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    @Builder
    public StudyGroupResponse(StudyGroup entity){
        this(
                entity.getId(),
                entity.getStudygroupName(),
                entity.getLimitedMemberSize(),
                entity.getUser().getId(),
                entity.getCreatedAt()
        );
    }



}
