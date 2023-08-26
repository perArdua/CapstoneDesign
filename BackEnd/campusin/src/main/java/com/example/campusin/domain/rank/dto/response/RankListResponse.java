package com.example.campusin.domain.rank.dto.response;

import com.example.campusin.domain.rank.Rank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RankListResponse {

    private Long rank;
    private String name;

    @Builder
    public RankListResponse(Long rank, String name) {
        this.rank = rank;
        this.name = name;
    }


    @Builder
    public RankListResponse(Rank rank) {
        this(
                rank.getStudyranking(),
                rank.getUser().getUsername()
        );
    }
}
