package com.example.campusin.domain.statistics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kok8454@gmail.com on 2023-06-02
 * Github : http://github.com/perArdua
 */

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "통계 식별자 응답")
public class StatisticsIdResponse {

    @Schema(description = "통계 식별자")
    private Long id;

    public StatisticsIdResponse(Long id) {
        this.id = id;
    }
}
