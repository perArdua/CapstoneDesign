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
    private Double score;

    @Builder
    public RankListResponse(Long rank, String name, int week, int month, Double score) {
        this.rank = rank;
        this.name = name;
        this.week = week;
        this.month = month;
        this.score = score;
    }
    // Ranks.totalElapsedTime 은 아카이버(RankArchiveService)/직접 생성 경로(RankService.createRank*)
    // 모두 이미 정규화된 학습시간 단위로 저장되므로(컴포지트 분리 이후), 응답 레이어에서는 추가 변환 없이 그대로 내보낸다.
    @Builder
    public RankListResponse(Ranks ranks) {
        this(
                ranks.getStudyRanking(),
                ranks.getUserName(),
                getWeekOfMonth(ranks.getWeekStartDate()),
                ranks.getWeekStartDate().getMonthValue(),
                ranks.getTotalElapsedTime() == null ? null : ranks.getTotalElapsedTime().doubleValue()
        );
    }
}
