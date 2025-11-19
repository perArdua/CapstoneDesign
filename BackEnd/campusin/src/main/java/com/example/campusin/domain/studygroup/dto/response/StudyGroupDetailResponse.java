package com.example.campusin.domain.studygroup.dto.response;

import com.example.campusin.domain.studygroup.StudyGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Schema(name = "StudyGroupDetailResponse", description = "Detailed response for Study Group")
public class StudyGroupDetailResponse {

    @Schema(description = "StudyGroup ID")
    private Long studyGroupId;

    @Schema(description = "StudyGroup Name")
    private String studyGroupName;

    @Schema(description = "StudyGroup Limited Member Size")
    private int limitedMemberSize;

    @Schema(description = "StudyGroup Current Member Size")
    private int currentMemberSize;

    @Schema(description = "StudyGroup Leader Name")
    private String leaderName;

    @Schema(description = "StudyGroup Created At")
    private LocalDateTime createdAt;

    @Schema(description = "List of StudyGroup Members")
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
