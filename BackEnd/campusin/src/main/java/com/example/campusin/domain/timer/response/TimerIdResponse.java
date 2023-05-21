package com.example.campusin.domain.timer.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */
@Data
@NoArgsConstructor
public class TimerIdResponse {

    private Long id;

    public TimerIdResponse(Long id) {
        this.id = id;
    }
}
