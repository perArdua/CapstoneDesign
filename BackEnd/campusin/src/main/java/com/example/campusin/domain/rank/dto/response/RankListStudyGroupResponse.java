package com.example.campusin.domain.rank.dto.response;
import com.example.campusin.domain.rank.Rank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RankListStudyGroupResponse {

    private Long rank;
    private String StudyGroupName;
    private int week;
    private int month;

    @Builder
    public RankListStudyGroupResponse(Long rank, String studyGroupName, int week, int month) {
        this.rank = rank;
        this.StudyGroupName = studyGroupName;
        this.week = week;
        this.month = month;
    }

    @Builder
    public RankListStudyGroupResponse(Rank rank){
        this(
                rank.getStudyranking(),
                rank.getStudyGroup().getStudygroupName(),
                rank.getStartDate(rank.getStatistics().getDate()).getDayOfMonth() / 7 + 1,
                rank.getStartDate(rank.getStatistics().getDate()).getMonthValue()
        );
    }
}
