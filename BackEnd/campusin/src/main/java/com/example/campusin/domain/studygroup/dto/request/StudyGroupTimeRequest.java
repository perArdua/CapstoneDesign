package com.example.campusin.domain.studygroup.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created by kok8454@gmail.com on 2023-09-02
 * Github : http://github.com/perArdua
 */

@Getter
@Setter
@NoArgsConstructor
@ApiModel(value = "StudyGroupTimeRequest", description = "StudyGroup Time Request")
public class StudyGroupTimeRequest {

    @ApiModelProperty (value = "StudyGroup ID")
    private Long studyGroupId;

    @ApiModelProperty (value = "End Date")
    private LocalDate endDate;
}
