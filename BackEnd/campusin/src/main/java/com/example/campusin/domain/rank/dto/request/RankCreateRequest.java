package com.example.campusin.domain.rank.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RankCreateRequest {

    private LocalDate localDate;

    public RankCreateRequest(LocalDate localDate) {
        this.localDate = localDate;
    }
}
