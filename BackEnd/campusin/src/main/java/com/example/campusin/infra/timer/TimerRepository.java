package com.example.campusin.infra.timer;

import com.example.campusin.domain.timer.Timer;
import com.example.campusin.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */
public interface TimerRepository extends JpaRepository<Timer, Long> {

    public Timer findTopByUserIdOrderByModifiedAtDesc(Long userId);

    public List<Timer> findAllByUserId(Long userId);
    @Query("select t from Timer t where t.user.id = :userId and DATE_FORMAT(t.modifiedAt, '%Y-%m-%d') >= :startDate AND DATE_FORMAT(t.modifiedAt, '%Y-%m-%d') <= :endDate")
    public List<Timer> findAllByUserAndModifiedAtBetween(@Param("userId") Long userId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select t from Timer t where t.user.id = :userId ")
    public Page<Timer> findAllMyTimer(@Param("userId") Long userId, Pageable pageable);
}
