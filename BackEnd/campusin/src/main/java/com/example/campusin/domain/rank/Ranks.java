package com.example.campusin.domain.rank;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.studygroup.StudyGroup;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

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
    private Long studyRanking;

    @Column(name = "question_ranking")
    private Long questionRanking;

    @Column(name = "user_name", unique = true)
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

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Builder
    public Ranks(Long studyRanking, Long questionRanking, String userName, Statistics statistics, StudyGroup studyGroup, Long totalElapsedTime, Long totalNumberOfQuestions, LocalDate weekStartDate) {
        this.studyRanking = studyRanking;
        this.questionRanking = questionRanking;
        this.userName = userName;
        this.statistics = statistics;
        this.studyGroup = studyGroup;
        this.totalElapsedTime = totalElapsedTime;
        this.totalNumberOfQuestions = totalNumberOfQuestions;
        this.weekStartDate = weekStartDate;
    }

    public void updateStudyRanking(Long ranking) {
        this.studyRanking = ranking;
    }
    public void updateQuestionRanking(Long ranking) {
        this.questionRanking = ranking;
    }
}
