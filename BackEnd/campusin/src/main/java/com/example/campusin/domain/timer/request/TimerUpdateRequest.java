package com.example.campusin.domain.timer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */
@Data
@NoArgsConstructor
@Schema(name = "TimerUpdateRequest", description = "타이머 업데이트 요청")
public class TimerUpdateRequest {

    @Schema(description = "경과 시간", required = true, example = "1000")
    private Long elapsedTime;

    public TimerUpdateRequest(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
