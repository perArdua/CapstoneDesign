package com.example.campusin.infra.rank;

import com.example.campusin.domain.rank.Ranks;
import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;



//@Query(value = "SELECT COUNT(p) FROM Post p WHERE p.user = :user AND p.board.boardType = 'Question' AND DATE_FORMAT(p.createdAt, '%Y-%m-%d') >= :startDate AND DATE_FORMAT(p.createdAt, '%Y-%m-%d') < :endDate")
//    long countQuestionsByUserAndModifiedAtBetween(@Param("user") User user, @Param("startDate") String startDate, @Param("endDate") String endDate);

@Repository
public interface RankRepository extends JpaRepository<Ranks, Long> {
    @Query(value = "SELECT r FROM Ranks r " +
            "WHERE r.studyGroup.id != null " +
            "AND DATE_FORMAT(r.statistics.date, '%Y-%m-%d') = :localDate " +
            "ORDER BY r.totalElapsedTime DESC")
    Page<Ranks> countInStudyGroup(@Param("localDate") String localDate, Pageable pageable);

    @Query(value = "select r from Ranks r where r.user = :user and r.statistics = :statistics and r.studyGroup.id = null")
    Ranks findByUserAndStatistics(User user, Statistics statistics);
    @Query(value = "select r from Ranks r where r.user = :user and r.statistics = :statistics and r.studyGroup.id = :studyGroupId")
    Ranks findByUserAndStatisticsAndStudyGroup(@Param("user") User user, @Param("statistics") Statistics statistics, @Param("studyGroupId") Long studyGroupId);
    @Query(value = "SELECT r FROM Ranks r " +
            "WHERE r.studyGroup.id = null " +
            "AND DATE_FORMAT(r.statistics.date, '%Y-%m-%d') = :localDate " +
            "ORDER BY r.totalElapsedTime DESC")
    Page<Ranks> findAllByOrderByTotalStudyTimeAsc(@Param("localDate") String localDate, Pageable pageable);

    @Query(value = "SELECT r FROM Ranks r " +
            "WHERE r.studyGroup.id = null " +
            "AND DATE_FORMAT(r.statistics.date, '%Y-%m-%d') = :localDate " +
            "ORDER BY r.totalNumberOfQuestions DESC")
    Page<Ranks> findAllByOrderByTotalNumberOfQuestionsAsc(@Param("localDate") String localDate, Pageable pageable);
}
