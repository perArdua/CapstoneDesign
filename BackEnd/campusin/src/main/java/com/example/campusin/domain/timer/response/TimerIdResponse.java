package com.example.campusin.domain.timer.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */
@Data
@NoArgsConstructor
@Schema(name = "TimerIdResponse", description = "타이머 아이디 응답")
public class TimerIdResponse {

    @Schema(description = "타이머 아이디", required = true, example = "1")
    private Long id;

    public TimerIdResponse(Long id) {
        this.id = id;
    }
}
