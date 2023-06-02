package com.example.campusin.domain.statistics;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by kok8454@gmail.com on 2023-06-02
 * Github : http://github.com/perArdua
 */
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE Statistics SET deleted_at = CURRENT_TIMESTAMP where statistics_id = ?")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Statistics extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "statistics_id")
    private Long id;

    @Column(name = "elapsed_time")
    private Long elapsedTime;

    @Column(name = "number_of_questions")
    private Long numberOfQuestions;

    @Column(name = "number_of_answers")
    private Long numberOfAnswers;

    @Column(name = "number_of_adopted_answers")
    private Long numberOfAdoptedAnswers;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Builder
    public Statistics(Long id, Long elapsedTime, Long numberOfQuestions, Long numberOfAnswers, Long numberOfAdoptedAnswers, LocalDate date, User user) {
        this.id = id;
        this.elapsedTime = elapsedTime;
        this.numberOfQuestions = numberOfQuestions;
        this.numberOfAnswers = numberOfAnswers;
        this.numberOfAdoptedAnswers = numberOfAdoptedAnswers;
        this.date = date;
        this.user = user;
    }

    public void updateElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void addElapsedTime(Long elapsedTime) {
        this.elapsedTime += elapsedTime;
    }

    public void updateNumberOfQuestions(Long numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public void updateNumberOfAnswers(Long numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public void updateNumberOfAdoptedAnswers(Long numberOfAdoptedAnswers) {
        this.numberOfAdoptedAnswers = numberOfAdoptedAnswers;
    }
}
