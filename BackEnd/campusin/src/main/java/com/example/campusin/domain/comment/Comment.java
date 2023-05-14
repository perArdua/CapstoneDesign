package com.example.campusin.domain.comment;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;

@Getter
@Entity
@Table(name = "COMMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE COMMENT SET deleted_at = CURRENT_TIMESTAMP where id = ?")

public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "comment_id")
    private Long id;
    @Column(name = "content", nullable = false, length = 300)
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDelete;

    @Builder
    public Comment(Comment parent,User user, Post post, String content) {

        setPost(post);
        this.user = user;
        setParent(parent);
        this.isDelete = false;
        this.content = content;
    }

    public void updateDelete() {
        this.isDelete = true;
    }

    public void setPost(Post post){
        if(Objects.nonNull(this.post)) {
            this.post.getCommentList().remove(this);
        }

        this.post = post;
        post.getCommentList().add(this);
    }

    public void setParent(Comment parent){
        if(Objects.isNull(parent)){
            return;
        }
        this.parent = parent;
        parent.getChildren().add(this);
    }

    public void addChildren(Comment child){
        child.setParent(this);
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Comment getParent() {
        return parent;
    }

    public List<Comment> getChildren() {
        return children;
    }
}
