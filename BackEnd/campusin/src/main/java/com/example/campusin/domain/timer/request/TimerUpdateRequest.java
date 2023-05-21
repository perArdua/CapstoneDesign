package com.example.campusin.domain.timer.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */
@Data
@NoArgsConstructor
public class TimerUpdateRequest {

    private Long elapsedTime;

    public TimerUpdateRequest(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
