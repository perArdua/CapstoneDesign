package com.example.campusin.domain.timer.response;

import com.example.campusin.domain.timer.Timer;
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
@ApiModel(value = "TimerResponse", description = "타이머 응답")
public class TimerResponse {

    @ApiModelProperty(value = "타이머 아이디", required = true, example = "1")
    private Long id;

    @ApiModelProperty(value = "과목명", required = true, example = "자료구조")
    private String subject;

    @ApiModelProperty(value = "경과 시간", required = true, example = "1000")
    private Long elapsedTime;

    @ApiModelProperty(value = "유저 아이디", required = true, example = "1")
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
