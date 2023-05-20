package com.example.campusin.domain.post;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.photo.Photo;
import com.example.campusin.domain.post.dto.request.PostUpdateRequest;
import com.example.campusin.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */

@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE post SET deleted_at = CURRENT_TIMESTAMP where post_id = ?")
@Table(name = "post")
@Getter
@NoArgsConstructor
@Entity
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "content", length = 65535)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", referencedColumnName = "board_id")
    private Board board;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

    private Integer commentCount = 0;
    private Integer likeCount = 0;


    @Builder
    public Post(String title, String content, User user, Board board) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.board = board;
    }

    public void updatePost(PostUpdateRequest request) {
        setTitle(request.getTitle());
        setContent(request.getContent());
        setPhotos(request.getPhotos());
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public void addPhoto(final Photo photo) {
        photos.add(photo);
        photo.setPost(this);
    }

    public void removePhoto(final Photo photo) {
        photos.remove(photo);
        photo.setPost(null);
    }
    
    public List<Comment> getCommentList() {
        return comments;
    }

    public void increaseCommentCount() {
        commentCount++;
    }

    public void increaseLikeCount() {
        likeCount++;
    }
}
