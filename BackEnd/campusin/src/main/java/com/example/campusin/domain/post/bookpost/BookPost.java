package com.example.campusin.domain.post.bookpost;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.user.Users;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by kok8454@gmail.com on 2023-05-03
 * Github : http://github.com/perArdua
 */
@Table(name = "BOOKPOST")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE BOOKPOST SET deleted_at = CURRENT_TIMESTAMP where id = ?")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BookPost extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "writer_id")
    private Users writer;

    @Column(length = 40, nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Builder
    public BookPost(Long id, String title, String content, Users writer) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.writer = writer;
    }
}