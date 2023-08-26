package com.example.campusin.infra.rank;

import com.example.campusin.domain.rank.Rank;
import com.example.campusin.domain.rank.dto.response.RankResponse;
import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {

    @Query(value = "SELECT COUNT(r) FROM Rank r WHERE r.statistics = :statistics AND r.totalElapsedTime > :totalStudyTime")
    Long countByStatisticsAndTotalStudyTimeGreaterThan(Statistics statistics, Long totalStudyTime);

    @Query(value = "SELECT COUNT(r) FROM Rank r WHERE r.statistics = :statistics AND r.totalNumberOfQuestions > :totalQuestion")
    Long countByStatisticsAndTotalQuestionGreaterThan(Statistics statistics, Long totalQuestion);

    @Query(value = "SELECT COUNT(r) FROM Rank r WHERE r.statistics = :statistics AND r.totalElapsedTime > :totalStudyTime AND r.studyGroup = :studyGroup")
    Long countByStatisticsAndTotalStudyTimeGreaterThanInStudyGroup(StudyGroup studyGroup, Statistics statistics, Long totalStudyTime);

    @Query(value = "select r from Rank r where r.user = :user and r.statistics = :statistics")
    Rank findByUserAndStatistics(User user, Statistics statistics);

    @Query(value = "select r from Rank r where r.statistics = :statistics and r.studyGroup = :studyGroup")
    StudyGroup findByStudyGroupAndStatistics(StudyGroup studyGroup, Statistics statistics);
}
