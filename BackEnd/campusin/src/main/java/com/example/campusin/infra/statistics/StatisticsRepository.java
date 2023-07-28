package com.example.campusin.infra.statistics;

import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

/**
 * Created by kok8454@gmail.com on 2023-06-02
 * Github : http://github.com/perArdua
 */
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    // -- MySQL
    //SELECT DATE_FORMAT(NOW(),'%Y-%m-%d') FROM DUAL;
    //
    //-- H2
    //SELECT FORMATDATETIME('2021-07-24 13:33:42', 'yyyy-MM-dd') FROM DUAL;
    @Query(value = "SELECT COUNT(p) FROM Post p WHERE p.user = :user AND p.board.boardType = 'Question' AND FORMATDATETIME(p.createdAt, 'yyyy-MM-dd') >= :startDate AND FORMATDATETIME(p.createdAt, 'yyyy-MM-dd') < :endDate")
    long countQuestionsByUserAndModifiedAtBetween(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

//    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user = :user AND c.createdAt >= :startDate AND c.createdAt < :endDate")
//    long countCommentsByUserAndDate(User user, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT COUNT(c) FROM Comment c WHERE c.user = :user AND FORMATDATETIME(c.createdAt, 'yyyy-MM-dd') >= :startDate AND FORMATDATETIME(c.createdAt, 'yyyy-MM-dd') < :endDate AND c.isAnswer = true")
    long countAnswersByUserAndModifiedAtBetweenAndIsAnswerTrue(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT COUNT(c) FROM Comment c WHERE c.user = :user AND FORMATDATETIME(c.createdAt, 'yyyy-MM-dd') >= :startDate AND FORMATDATETIME(c.createdAt, 'yyyy-MM-dd') < :endDate AND c.isAdopted = true")
    long countAnswersByUserAndModifiedAtBetweenAndIsAdoptedTrue(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT s FROM Statistics s WHERE s.date = :date AND s.user = :user")
    Statistics findByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);
}
