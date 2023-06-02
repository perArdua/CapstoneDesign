package com.example.campusin.domain.studygroup.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "StudyGroup id 응답", description = "StudyGroup id를 반환한다.")
public class StudyGroupIdResponse {

    @ApiModelProperty(value = "StudyGroup id", example = "1")
    private Long id;
    public StudyGroupIdResponse(Long id) {
        this.id = id;
    }
}
