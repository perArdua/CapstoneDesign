package com.example.campusin.domain.studygroup.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "StudyGroupTimeRequest", description = "StudyGroup Time Request")
public class StudyGroupTimeRequest {

    @Schema(name = "StudyGroup ID")
    private Long studyGroupId;

    @Schema(name = "End Date")
    private LocalDate endDate;
}
