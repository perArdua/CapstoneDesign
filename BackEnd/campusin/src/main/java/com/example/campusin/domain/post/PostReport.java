package com.example.campusin.domain.post;

/**
 * Created by kok8454@gmail.com on 2023-09-09
 * Github : http://github.com/perArdua
 */

import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "POST_REPORT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter

public class PostReport extends BaseTimeEntity {

    @EmbeddedId
    private PostReportId id;

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(column = @JoinColumn(name = "post_id", referencedColumnName = "post_id"))
    private Post post;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(column = @JoinColumn(name = "user_id", referencedColumnName = "user_id"))
    private User user;

    public PostReport(Post post, User user) {
        setPost(post);
        setUser(user);
        this.id = new PostReportId(user.getId(), post.getId());
    }

    private void setPost(Post post) {
        if(Objects.isNull(post)) {
            throw new IllegalArgumentException("Post must not be null");
        }
        this.post = post;
        post.increaseReportCount();
    }

    private void setUser(User user) {
        if(Objects.isNull(user)) {
            throw new IllegalArgumentException("User must not be null");
        }
        this.user = user;
    }
}
