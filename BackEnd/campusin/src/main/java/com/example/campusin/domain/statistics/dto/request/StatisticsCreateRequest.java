package com.example.campusin.domain.statistics.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created by kok8454@gmail.com on 2023-06-02
 * Github : http://github.com/perArdua
 */

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "통계 생성 요청")
public class StatisticsCreateRequest {

    @Schema(description = "통계 생성 날짜")
    private LocalDate localDate;

    public StatisticsCreateRequest(LocalDate localDate) {
        this.localDate = localDate;
    }
}
