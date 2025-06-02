package com.example.campusin.domain.studygroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "StudyGroup id 응답", description = "StudyGroup id를 반환한다.")
public class StudyGroupIdResponse {

    @Schema(description = "StudyGroup id", example = "1")
    private Long id;
    public StudyGroupIdResponse(Long id) {
        this.id = id;
    }
}
