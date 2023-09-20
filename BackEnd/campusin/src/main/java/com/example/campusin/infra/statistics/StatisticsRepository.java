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

    @Query(value = "SELECT COUNT(p) FROM Post p WHERE p.user = :user AND p.board.boardType = 'Question' AND DATE_FORMAT(p.createdAt, '%Y-%m-%d') >= :startDate AND DATE_FORMAT(p.createdAt, '%Y-%m-%d') < :endDate")
    long countQuestionsByUserAndModifiedAtBetween(@Param("user") User user, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(value = "SELECT COUNT(c) FROM Comment c WHERE c.user = :user AND DATE_FORMAT(c.createdAt, '%Y-%m-%d') >= :startDate AND DATE_FORMAT(c.createdAt, '%Y-%m-%d') < :endDate AND c.isAnswer = true")
    long countAnswersByUserAndModifiedAtBetweenAndIsAnswerTrue(@Param("user") User user, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(value = "SELECT COUNT(c) FROM Comment c WHERE c.user = :user AND DATE_FORMAT(c.createdAt, '%Y-%m-%d') >= :startDate AND DATE_FORMAT(c.createdAt, '%Y-%m-%d') < :endDate AND c.isAdopted = true")
    long countAnswersByUserAndModifiedAtBetweenAndIsAdoptedTrue(@Param("user") User user, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(value = "SELECT s FROM Statistics s WHERE DATE_FORMAT(s.date, '%Y-%m-%d') = DATE_FORMAT(:date, '%Y-%m-%d') AND s.user = :user")
    Statistics findByUserAndDate(@Param("user") User user, @Param("date") String date);

}
