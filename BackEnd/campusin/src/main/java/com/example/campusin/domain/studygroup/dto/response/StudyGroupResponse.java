package com.example.campusin.domain.studygroup.dto.response;

import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.studygroup.StudyGroupMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@ApiModel(value = "StudyGroup 응답", description = "StudyGroup 응답")
public class StudyGroupResponse {

    @ApiModelProperty(value = "StudyGroup id", example = "1")
    private Long id;

    @ApiModelProperty(value = "StudyGroup name", example = "알고리즘스터디")
    private String studygroupName;

    @ApiModelProperty(value = "StudyGroup 제한인원", example = "5")
    private int limitedMemberSize;

    @ApiModelProperty(value = "StudyGroup 현재인원", example = "3")
    private int CurrentMemberSize;

    @ApiModelProperty(value = "StudyGroup user Name", example = "1")
    private String userName;

    @ApiModelProperty(value = "StudyGroup createdAt", example = "2021-05-08T00:00:00")
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
