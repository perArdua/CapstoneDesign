package com.example.campusin.domain.studygroup.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(value = "StudyGroup 생성 요청", description = "StudyGroup name, StudyGroup limitedMemberSize")
public class StudyGroupCreateRequest {
    @NotNull
    @ApiModelProperty(value = "StudyGroup name")
    private String studygroupName;
    @NotNull
    @ApiModelProperty(value = "StudyGroup limitedMemberSize")
    private int LimitedMemberSize;

}
