package com.example.campusin.domain.badge;

import com.example.campusin.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * Created by kok8454@gmail.com on 2023-06-05
 * Github : http://github.com/perArdua
 */

@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE badge SET deleted_at = CURRENT_TIMESTAMP where badge_id = ?")
@Table(name = "badge")
@Getter
@NoArgsConstructor
@Entity
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "badge_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Builder
    public Badge(String name, User user) {
        this.name = name;
        this.user = user;
    }
}
