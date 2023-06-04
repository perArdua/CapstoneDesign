package com.example.campusin.domain.studygroup.dto.response;

import com.example.campusin.domain.studygroup.StudyGroupMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@ApiModel(value = "StudyGroupMember 응답", description = "StudyGroupMember 응답")
public class StudyGroupMemberResponse {

    @ApiModelProperty(value = "StudyGroupMember 이름", example = "김태호")
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
