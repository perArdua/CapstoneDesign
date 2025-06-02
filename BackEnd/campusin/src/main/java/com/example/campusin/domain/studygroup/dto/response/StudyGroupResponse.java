package com.example.campusin.domain.studygroup.dto.response;

import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.studygroup.StudyGroupMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(name = "StudyGroup 응답", description = "StudyGroup 응답")
public class StudyGroupResponse {

    @Schema(description = "StudyGroup id", example = "1")
    private Long id;

    @Schema(description = "StudyGroup name", example = "알고리즘스터디")
    private String studygroupName;

    @Schema(description = "StudyGroup 제한인원", example = "5")
    private int limitedMemberSize;

    @Schema(description = "StudyGroup 현재인원", example = "3")
    private int CurrentMemberSize;

    @Schema(description = "StudyGroup user Name", example = "1")
    private String userName;

    @Schema(description = "StudyGroup createdAt", example = "2021-05-08T00:00:00")
    private LocalDateTime createdAt;

    @Builder
    public StudyGroupResponse(Long id, String studygroupName, int limitedMemberSize,int CurrentMemberSize, String userName, LocalDateTime createdAt) {
        this.id = id;
        this.studygroupName = studygroupName;
        this.limitedMemberSize = limitedMemberSize;
        this.CurrentMemberSize = CurrentMemberSize;
        this.userName = userName;
        this.createdAt = createdAt;
    }

    @Builder
    public StudyGroupResponse(StudyGroup entity){
        this(
                entity.getId(),
                entity.getStudygroupName(),
                entity.getLimitedMemberSize(),
                entity.getCurrentMemberSize(),
                entity.getUser().getNickname(),
                entity.getCreatedAt()
        );
    }

    @Builder
    public StudyGroupResponse(StudyGroupMember studyGroupMember){
        this(
                studyGroupMember.getStudyGroupId().getId(),
                studyGroupMember.getStudyGroupId().getStudygroupName(),
                studyGroupMember.getStudyGroupId().getLimitedMemberSize(),
                studyGroupMember.getStudyGroupId().getCurrentMemberSize(),
                studyGroupMember.getUser().getNickname(),
                studyGroupMember.getStudyGroupId().getCreatedAt()
        );
    }



}
