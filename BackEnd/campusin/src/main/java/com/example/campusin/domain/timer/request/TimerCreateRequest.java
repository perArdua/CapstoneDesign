package com.example.campusin.domain.timer.request;

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
public class TimerCreateRequest {

    private String subject;

    @Builder
    public TimerCreateRequest(String subject) {
        this.subject = subject;
    }
}
