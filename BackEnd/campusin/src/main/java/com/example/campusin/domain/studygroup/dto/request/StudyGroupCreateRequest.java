package com.example.campusin.domain.studygroup.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "StudyGroup 생성 요청", description = "StudyGroup name, StudyGroup limitedMemberSize")
public class StudyGroupCreateRequest {
    @NotNull
    @Schema(description = "StudyGroup name")
    private String studygroupName;
    @NotNull
    @Schema(description = "StudyGroup limitedMemberSize")
    private int LimitedMemberSize;

}
