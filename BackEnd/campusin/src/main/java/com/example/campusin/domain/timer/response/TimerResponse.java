package com.example.campusin.domain.timer.response;

import com.example.campusin.domain.timer.Timer;
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
public class TimerResponse {

    private Long id;
    private String subject;
    private Long elapsedTime;
    private Long userId;

    @Builder
    public TimerResponse(Long id, String subject, Long elapsedTime, Long userId) {
        this.id = id;
        this.subject = subject;
        this.elapsedTime = elapsedTime;
        this.userId = userId;
    }

    public TimerResponse(Timer timer) {
        this(
                timer.getId(),
                timer.getSubject(),
                timer.getElapsedTime(),
                timer.getUser().getId()
        );
    }
}
