package com.example.campusin.domain.statistics.dto.response;

import com.example.campusin.domain.statistics.Statistics;
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
public class StatisticsResponse {

    private Long totalElapsedTime;
    private Long numberOfQuestions;
    private Long numberOfAnswers;
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
