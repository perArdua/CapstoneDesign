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
public class StatisticsCreateRequest {

    private LocalDate localDate;

    public StatisticsCreateRequest(LocalDate localDate) {
        this.localDate = localDate;
    }
}
