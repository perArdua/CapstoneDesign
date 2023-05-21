package com.example.campusin.domain.timer.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */
@Data
@NoArgsConstructor
@ApiModel(value = "TimerUpdateRequest", description = "타이머 업데이트 요청")
public class TimerUpdateRequest {

    @ApiModelProperty(value = "경과 시간", required = true, example = "1000")
    private Long elapsedTime;

    public TimerUpdateRequest(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
