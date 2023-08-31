package com.example.campusin.infra.rank;

import com.example.campusin.domain.rank.Rank;
import com.example.campusin.domain.rank.dto.response.RankResponse;
import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {
    @Query(value = "SELECT r FROM Rank r WHERE r.studyGroup.id = :studyGroup order by r.totalElapsedTime desc")
    Page<Rank> countInStudyGroup(Long studyGroup, Pageable pageable);

    @Query(value = "select r from Rank r where r.user = :user and r.statistics = :statistics")
    Rank findByUserAndStatistics(User user, Statistics statistics);

    @Query("SELECT r FROM Rank r WHERE r.week = :week")
    Page<Rank> findAllByWeek(int week, Pageable pageable);

    @Query(value = "select r from Rank r where r.user = :user and r.statistics = :statistics and r.studyGroup.id = :studyGroupId")
    Rank findByUserAndStatisticsAndStudyGroup(User user, Statistics statistics, Long studyGroupId);

    @Query(value = "select r from Rank r  where r.studyGroup.id = null order by r.totalElapsedTime desc")
    Page<Rank> findAllByOrderByTotalStudyTimeAsc(Pageable pageable);

    @Query(value = "select r from Rank r order by r.totalNumberOfQuestions desc")
    Page<Rank> findAllByOrderByTotalNumberOfQuestionsAsc(Pageable pageable);
}
