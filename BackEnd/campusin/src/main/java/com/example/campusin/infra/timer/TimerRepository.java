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
    @Query("select t from Timer t where t.user = :user and FORMATDATETIME(t.modifiedAt, 'yyyy-MM-dd') >= :startDate AND FORMATDATETIME(t.modifiedAt, 'yyyy-MM-dd') < :endDate")
    public List<Timer> findAllByUserAndModifiedAtBetween(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
