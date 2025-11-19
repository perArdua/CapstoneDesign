package com.example.campusin.domain.studygroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by kok8454@gmail.com on 2023-09-02
 * Github : http://github.com/perArdua
 */
@Getter
@NoArgsConstructor
@Schema(name = "StudyGroupTimeReseponse", description = "StudyTime of StudyGroup Member")
public class StudyGroupTimeResponse {

    @Schema(description = "Study Group Member Name")
    private String studyGroupMemberName;

    @Schema(description = "Study Time of the week")
    private Long elapsedTime;

    public StudyGroupTimeResponse(String studyGroupMemberName, Long elapsedTime) {
        this.studyGroupMemberName = studyGroupMemberName;
        this.elapsedTime = elapsedTime;
    }
}

