package com.example.campusin.domain.post;

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
@Table(name = "POST_LIKE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE POST_LIKE SET deleted_at = CURRENT_TIMESTAMP where post_id = ? and user_id = ?")
public class PostLike extends BaseTimeEntity {

    @EmbeddedId
    private PostLikeId id;

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(column = @JoinColumn(name = "post_id", referencedColumnName = "post_id"))
    private Post post;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(column = @JoinColumn(name = "user_id", referencedColumnName = "user_id"))
    private User user;

    public PostLike(Post post, User user) {
        setPost(post);
        setUser(user);
        this.id = new PostLikeId(user.getId(), post.getId());
    }

    private void setPost(Post post) {
        if(Objects.isNull(post)) {
            throw new IllegalArgumentException("Post must not be null");
        }
        this.post = post;
        post.increaseLikeCount();
    }

    private void setUser(User user) {
        if(Objects.isNull(user)) {
            throw new IllegalArgumentException("User must not be null");
        }
        this.user = user;
    }
}
