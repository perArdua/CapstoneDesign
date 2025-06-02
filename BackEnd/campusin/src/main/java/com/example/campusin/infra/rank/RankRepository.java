package com.example.campusin.infra.rank;

import com.example.campusin.domain.rank.Ranks;
import com.example.campusin.domain.statistics.Statistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;



@Repository
public interface RankRepository extends JpaRepository<Ranks, Long> {

    Page<Ranks> findByStatistics_DateAndStudyGroupIsNotNullOrderByTotalElapsedTimeDesc(LocalDate localDate, Pageable pageable);

    Ranks findByUserNameAndStatisticsAndStudyGroupIsNull(String userName, Statistics statistics);

    Ranks findByUserNameAndStatisticsAndStudyGroupId(String userName, Statistics statistics, Long studyGroupId);

    Page<Ranks> findByStatistics_DateAndStudyGroupIsNullOrderByTotalNumberOfQuestionsDesc(LocalDate localDate, Pageable pageable);

    Page<Ranks> findAllByWeekStartDateOrderByStudyRankingAsc(LocalDate weekStartDate, Pageable pageable);
}
