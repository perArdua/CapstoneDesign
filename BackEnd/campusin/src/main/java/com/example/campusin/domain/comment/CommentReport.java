package com.example.campusin.domain.comment;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.springframework.util.Assert;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "COMMENT_REPORT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentReport extends BaseTimeEntity {

    @EmbeddedId
    private CommentReportId id;

    @MapsId("userId")
    @ManyToOne
    @JoinColumnOrFormula(column = @JoinColumn(name = "user_id", referencedColumnName = "user_id"))
    private User user;

    @MapsId("commentId")
    @ManyToOne(fetch = LAZY)
    @JoinColumnOrFormula(column = @JoinColumn(name = "comment_id", referencedColumnName = "comment_id"))
    private Comment comment;

    public CommentReport(User user, Comment comment) {
        Assert.notNull(user, "사용자는 필수입니다");
        Assert.notNull(comment, "댓글은 필수입니다");
        this.id = new CommentReportId(user.getId(), comment.getId());
        this.user = user;
        setComment(comment);
    }

    public void setComment(Comment comment){
        this.comment = comment;
    }

    public void setUser(User user){
        this.user = user;
    }
}
