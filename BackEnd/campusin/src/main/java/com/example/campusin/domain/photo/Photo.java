package com.example.campusin.domain.photo;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.post.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE photo SET deleted_at = CURRENT_TIMESTAMP where photo_id = ?")
@Getter
@NoArgsConstructor
@Entity
public class Photo extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "photo_id")
    private Long id;

    @Column(name = "path", nullable = false, length = 300)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    private Post post;

    public void setPost(final Post post) {
        this.post = post;
    }

    public Photo(String content) {
        this.content = content;
    }
}
