package com.example.campusin.infra.timer;

import com.example.campusin.domain.timer.Timer;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import com.example.campusin.support.H2Functions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TimerRepository")
class TimerRepositoryTest extends DataJpaTestSupport {

    @Autowired
    TimerRepository timerRepository;

    @BeforeEach
    void registerDateFormatFunction() {
        String aliasTarget = H2Functions.class.getName() + ".dateFormat";
        em.getEntityManager().createNativeQuery(
                        "CREATE ALIAS IF NOT EXISTS DATE_FORMAT FOR \"" + aliasTarget + "\"")
                .executeUpdate();
    }

    @Test
    @DisplayName("사용자별 타이머 조회 메서드들을 실행한다")
    void timerQueries() {
        // given
        User owner = TestEntityFactory.persistUser(em, "timer-user");
        Timer t1 = timerRepository.save(Timer.builder().user(owner).subject("s1").elapsedTime(10L).build());
        Timer t2 = timerRepository.save(Timer.builder().user(owner).subject("s2").elapsedTime(20L).build());
        em.flush();

        // when
        Timer latest = timerRepository.findTopByUserIdOrderByModifiedAtDesc(owner.getId());
        List<Timer> byUser = timerRepository.findAllByUserId(owner.getId());
        Page<Timer> page = timerRepository.findAllMyTimer(owner.getId(), PageRequest.of(0, 10));

        // then
        assertThat(latest.getId()).isIn(t1.getId(), t2.getId());
        assertThat(byUser).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("기간 내 사용자 타이머를 조회한다")
    void findAllByUserAndModifiedAtBetween() {
        // given
        User owner = TestEntityFactory.persistUser(em, "timer-range");
        timerRepository.save(Timer.builder().user(owner).subject("range1").elapsedTime(10L).build());
        timerRepository.save(Timer.builder().user(owner).subject("range2").elapsedTime(20L).build());
        em.flush();
        String today = LocalDate.now().toString();

        // when
        List<Timer> timers = timerRepository.findAllByUserAndModifiedAtBetween(owner.getId(), today, today);

        // then
        assertThat(timers).hasSize(2);
        assertThat(timers).allMatch(timer -> timer.getUser().getId().equals(owner.getId()));
    }
}
