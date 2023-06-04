package com.example.campusin.domain.studygroup.dto.response;

import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.studygroup.StudyGroupMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@ApiModel(value = "StudyGroupDetailResponse", description = "Detailed response for Study Group")
public class StudyGroupDetailResponse {

    @ApiModelProperty(value = "StudyGroup ID")
    private Long studyGroupId;

    @ApiModelProperty(value = "StudyGroup Name")
    private String studyGroupName;

    @ApiModelProperty(value = "StudyGroup Limited Member Size")
    private int limitedMemberSize;

    @ApiModelProperty(value = "StudyGroup Current Member Size")
    private int currentMemberSize;

    @ApiModelProperty(value = "StudyGroup Leader Name")
    private String leaderName;

    @ApiModelProperty(value = "StudyGroup Created At")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "List of StudyGroup Members")
    private List<StudyGroupMemberResponse> memberList;

    @Builder
    public StudyGroupDetailResponse(Long studyGroupId, String studyGroupName, int limitedMemberSize,
                                    int currentMemberSize, String leaderName, LocalDateTime createdAt,
                                    List<StudyGroupMemberResponse> memberList) {
        this.studyGroupId = studyGroupId;
        this.studyGroupName = studyGroupName;
        this.limitedMemberSize = limitedMemberSize;
        this.currentMemberSize = currentMemberSize;
        this.leaderName = leaderName;
        this.createdAt = createdAt;
        this.memberList = memberList;
    }

    @Builder
    public StudyGroupDetailResponse(StudyGroup studyGroup) {
        this(
                studyGroup.getId(),
                studyGroup.getStudygroupName(),
                studyGroup.getLimitedMemberSize(),
                studyGroup.getCurrentMemberSize(),
                studyGroup.getUser().getNickname(),
                studyGroup.getCreatedAt(),
                studyGroup.getMembers().stream().map(StudyGroupMemberResponse::new).collect(Collectors.toList())

        );
    }
}
