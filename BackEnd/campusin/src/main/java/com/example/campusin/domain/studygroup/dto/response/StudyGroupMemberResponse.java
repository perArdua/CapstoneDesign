package com.example.campusin.domain.studygroup.dto.response;

import com.example.campusin.domain.studygroup.StudyGroupMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(name = "StudyGroupMember 응답", description = "StudyGroupMember 응답")
public class StudyGroupMemberResponse {

    @Schema(description = "StudyGroupMember 이름", example = "김태호")
    private String memberName;


    @Builder
    public StudyGroupMemberResponse(String memberName) {
        this.memberName = memberName;
    }

    @Builder
    public StudyGroupMemberResponse(StudyGroupMember studyGroupMember) {
        this(
                studyGroupMember.getUser().getNickname()
        );
    }

}
