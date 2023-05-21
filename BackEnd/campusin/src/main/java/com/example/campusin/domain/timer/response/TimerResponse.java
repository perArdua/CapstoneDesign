package com.example.campusin.domain.timer.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimerResponse {

    private Long id;
    private String subject;
    private Long elapsedTime;
    private String loginId;

    @Builder
    public TimerResponse(Long id, String subject, Long elapsedTime, String loginId) {
        this.id = id;
        this.subject = subject;
        this.elapsedTime = elapsedTime;
        this.loginId = loginId;
    }
}
