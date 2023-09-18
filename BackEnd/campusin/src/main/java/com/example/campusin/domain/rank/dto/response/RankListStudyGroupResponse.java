package com.example.campusin.domain.rank.dto.response;
import com.example.campusin.domain.rank.Ranks;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RankListStudyGroupResponse {

    private Long rank;
    private String name;
    private int week;
    private int month;

    @Builder
    public RankListStudyGroupResponse(Long rank, String studyGroupName, int week, int month) {
        this.rank = rank;
        this.name = studyGroupName;
        this.week = week;
        this.month = month;
    }

    @Builder
    public RankListStudyGroupResponse(Ranks ranks){
        this(
                ranks.getStudyranking(),
                ranks.getStudyGroup().getStudygroupName(),
                ranks.getStartDate(ranks.getStatistics().getDate()).getDayOfMonth() / 7 + 1,
                ranks.getStartDate(ranks.getStatistics().getDate()).getMonthValue()
        );
    }
}
