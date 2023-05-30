package com.example.campusin.domain.studygroup.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class StudyGroupCreateRequest {
    @NotNull
    private String studygroupName;
    @NotNull
    private int LimitedMemberSize;

    private Long userId;


}
