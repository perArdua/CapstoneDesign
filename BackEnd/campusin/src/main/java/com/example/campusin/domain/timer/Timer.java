package com.example.campusin.domain.timer;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.timer.request.TimerUpdateRequest;
import com.example.campusin.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */

@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE TIMER SET deleted_at = CURRENT_TIMESTAMP where timer_id = ?")
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Timer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "timer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "subject")
    private String subject;

    @Column(name = "elapsed_time")
    private Long elapsedTime;

    @Builder
    public Timer(Long id, User user, String subject, Long elapsedTime) {
        this.id = id;
        this.user = user;
        this.subject = subject;
        this.elapsedTime = elapsedTime;
    }

    public void updateTimer(TimerUpdateRequest timerUpdateRequest){
        this.elapsedTime += timerUpdateRequest.getElapsedTime();
    }
}
