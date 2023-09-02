package com.example.campusin.domain.studygroup.dto.response;

import com.example.campusin.domain.studygroup.StudyGroupMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by kok8454@gmail.com on 2023-09-02
 * Github : http://github.com/perArdua
 */
@Getter
@NoArgsConstructor
@ApiModel(value = "StudyGroupTimeReseponse", description = "StudyTime of StudyGroup Member")
public class StudyGroupTimeResponse {

    @ApiModelProperty(value = "Study Group Member")
    private StudyGroupMember studyGroupMember;

    @ApiModelProperty(value = "Study Time of the week")
    private Long elapsedTime;

    public StudyGroupTimeResponse(StudyGroupMember studyGroupMember, Long elapsedTime) {
        this.studyGroupMember = studyGroupMember;
        this.elapsedTime = elapsedTime;
    }
}

