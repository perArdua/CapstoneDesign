package com.example.campusin.domain.timer.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */

@Data
@NoArgsConstructor
@ApiModel(value = "TimerCreateRequest", description = "타이머 생성 요청")
public class TimerCreateRequest {

    @ApiModelProperty(value = "과목명", required = true, example = "자료구조")
    private String subject;

    @Builder
    public TimerCreateRequest(String subject) {
        this.subject = subject;
    }
}
