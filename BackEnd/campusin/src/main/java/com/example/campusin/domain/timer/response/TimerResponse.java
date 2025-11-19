package com.example.campusin.domain.timer.response;

import com.example.campusin.domain.timer.Timer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */

@Data
@NoArgsConstructor
@Schema(name = "TimerResponse", description = "타이머 응답")
public class TimerResponse {

    @Schema(description = "타이머 아이디", required = true, example = "1")
    private Long id;

    @Schema(description = "과목명", required = true, example = "자료구조")
    private String subject;

    @Schema(description = "경과 시간", required = true, example = "1000")
    private Long elapsedTime;

    @Schema(description = "유저 아이디", required = true, example = "1")
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
