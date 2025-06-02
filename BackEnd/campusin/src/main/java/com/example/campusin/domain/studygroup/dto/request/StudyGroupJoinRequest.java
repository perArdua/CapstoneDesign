package com.example.campusin.domain.studygroup.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "StudyGroup 가입 요청", description = "StudyGroupId")
public class StudyGroupJoinRequest {
    @NotNull
    @Schema(description = "StudyGroup id")
    private Long studygroupId;


}
