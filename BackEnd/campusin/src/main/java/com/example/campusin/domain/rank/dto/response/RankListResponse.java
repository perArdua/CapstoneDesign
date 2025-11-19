package com.example.campusin.domain.rank.dto.response;

import com.example.campusin.domain.rank.Ranks;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.example.campusin.common.utils.WeekUtil.getWeekOfMonth;

@Getter
@NoArgsConstructor
public class RankListResponse {

    private Long rank;
    private String name;
    private int week;
    private int month;

    @Builder
    public RankListResponse(Long rank, String name, int week, int month) {
        this.rank = rank;
        this.name = name;
        this.week = week;
        this.month = month;
    }
    @Builder
    public RankListResponse(Ranks ranks) {
        this(
                ranks.getStudyRanking(),
                ranks.getUserName(),
                getWeekOfMonth(ranks.getWeekStartDate()),
                ranks.getWeekStartDate().getMonthValue()
        );
    }
}
