package com.example.campusin.domain.rank.dto.response;

import com.example.campusin.domain.rank.Rank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RankResponse {

    private Long rank;

    private int week;

    @Builder
    public RankResponse(Long rank, int week) {
        this.rank = rank;
        this.week = week;
    }

}
