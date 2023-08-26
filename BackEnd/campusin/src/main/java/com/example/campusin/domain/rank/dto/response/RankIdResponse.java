package com.example.campusin.domain.rank.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RankIdResponse {

    private Long id;

    public RankIdResponse(Long id) {
        this.id = id;
    }
}
