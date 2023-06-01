package com.example.campusin.infra.timer;

import com.example.campusin.domain.timer.Timer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */
public interface TimerRepository extends JpaRepository<Timer, Long> {

    public Timer findTopByUserIdOrderByModifiedAtDesc(Long userId);
}
