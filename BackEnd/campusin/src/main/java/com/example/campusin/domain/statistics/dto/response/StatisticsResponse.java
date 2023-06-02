package com.example.campusin.domain.statistics.dto.response;

import com.example.campusin.domain.statistics.Statistics;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kok8454@gmail.com on 2023-06-02
 * Github : http://github.com/perArdua
 */

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "통계 응답")
public class StatisticsResponse {

    @Schema(description = "총 학습 시간")
    private Long totalElapsedTime;

    @Schema(description = "질문 수")
    private Long numberOfQuestions;

    @Schema(description = "답변 수")
    private Long numberOfAnswers;

    @Schema(description = "채택된 답변 수")
    private Long numberOfAdoptedAnswers;

    @Builder
    public StatisticsResponse(Long totalElapsedTime, Long numberOfQuestions, Long numberOfAnswers, Long numberOfAdoptedAnswers) {
        this.totalElapsedTime = totalElapsedTime;
        this.numberOfQuestions = numberOfQuestions;
        this.numberOfAnswers = numberOfAnswers;
        this.numberOfAdoptedAnswers = numberOfAdoptedAnswers;
    }

    @Builder
    public StatisticsResponse(Statistics statistics) {
        this.totalElapsedTime = statistics.getElapsedTime();
        this.numberOfQuestions = statistics.getNumberOfQuestions();
        this.numberOfAnswers = statistics.getNumberOfAnswers();
        this.numberOfAdoptedAnswers = statistics.getNumberOfAdoptedAnswers();
    }
}
