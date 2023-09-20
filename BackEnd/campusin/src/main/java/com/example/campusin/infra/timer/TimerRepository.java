package com.example.campusin.infra.timer;

import com.example.campusin.domain.timer.Timer;
import com.example.campusin.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */
public interface TimerRepository extends JpaRepository<Timer, Long> {

    public Timer findTopByUserIdOrderByModifiedAtDesc(Long userId);

    public List<Timer> findAllByUserId(Long userId);
    @Query("select t from Timer t where t.user = :user and DATE_FORMAT(t.modifiedAt, '%Y-%m-%d') >= :startDate AND DATE_FORMAT(t.modifiedAt, '%Y-%m-%d') < :endDate")
    public List<Timer> findAllByUserAndModifiedAtBetween(@Param("user") User user, @Param("startDate") String startDate, @Param("endDate") String endDate);
}
