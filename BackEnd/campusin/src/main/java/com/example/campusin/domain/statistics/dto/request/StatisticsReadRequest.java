package com.example.campusin.domain.statistics.dto.request;

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

public class StatisticsReadRequest {

    private Long id;
    private LocalDate localDate;

    public StatisticsReadRequest(Long id, LocalDate localDate) {
        this.id = id;
        this.localDate = localDate;
    }
}
