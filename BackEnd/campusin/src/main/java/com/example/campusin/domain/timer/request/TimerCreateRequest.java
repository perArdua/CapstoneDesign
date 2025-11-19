package com.example.campusin.domain.timer.request;

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
@Schema(name = "TimerCreateRequest", description = "타이머 생성 요청")
public class TimerCreateRequest {

    @Schema(description = "과목명", required = true, example = "자료구조")
    private String subject;

    @Builder
    public TimerCreateRequest(String subject) {
        this.subject = subject;
    }
}
