package com.example.campusin.domain.rank;
import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE ranks SET deleted_at = CURRENT_TIMESTAMP where rank_id = ?")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Ranks extends BaseTimeEntity{

    @Id
    @Column(name = "rank_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "study_ranking")
    private Long Studyranking;

    @Column(name = "question_ranking")
    private Long QuestionRanking;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "user_name")
    private String userName;

    @ManyToOne
    @JoinColumn(name = "statistics_id")
    private Statistics statistics;

    @ManyToOne
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroup;

    @Column(name = "total_elapsed_time")
    private Long totalElapsedTime;

    @Column(name = "total_number_of_questions")
    private Long totalNumberOfQuestions;

    @Column(name = "week")
    private int week;

    @Builder
    public Ranks(Long Studyranking, Long QuestionRanking, User user, String userName, Statistics statistics, StudyGroup studyGroup, Long totalElapsedTime, Long totalNumberOfQuestions, int week) {
        this.Studyranking = Studyranking;
        this.QuestionRanking = QuestionRanking;
        this.user = user;
        this.userName = userName;
        this.statistics = statistics;
        this.studyGroup = studyGroup;
        this.totalElapsedTime = totalElapsedTime;
        this.totalNumberOfQuestions = totalNumberOfQuestions;
        this.week = week;
    }

    public void updateStudyRanking(Long ranking) {
        this.Studyranking = ranking;
    }
    public void updateQuestionRanking(Long ranking) {
        this.QuestionRanking = ranking;
    }
    public LocalDate getStartDate(LocalDate localDate) {
        while (localDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
            localDate = localDate.minusDays(1);
        }
        return localDate;
    }
}
