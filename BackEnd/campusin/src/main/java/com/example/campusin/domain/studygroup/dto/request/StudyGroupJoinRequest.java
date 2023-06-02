package com.example.campusin.domain.studygroup.dto.request;

import com.example.campusin.domain.studygroup.StudyGroupMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(value = "StudyGroup 가입 요청", description = "StudyGroupId")
public class StudyGroupJoinRequest {
    @NotNull
    @ApiModelProperty(value = "StudyGroup id")
    private Long studygroupId;


}
