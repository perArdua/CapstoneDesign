package com.example.campusin.domain.post;

import com.example.campusin.domain.basetime.BaseTimeEntity;
import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.photo.Photo;
import com.example.campusin.domain.post.dto.request.PostUpdateRequest;
import com.example.campusin.domain.tag.Tag;
import com.example.campusin.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
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

    @Column(name = "content", length = 10000)
    private String content;

    @Column(name = "price")
    @ColumnDefault("0")
    private Long price;

    @Column(name = "study_group_id")
    @ColumnDefault("-1")
    private Long studyGroupId;

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
    private Integer reportCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", referencedColumnName = "tag_id")
    private Tag tag;

    private Boolean isBadgeAccepted = null;

    @Builder
    public Post(String title, String content, User user, Board board, Long price, Long studyGroupId, Tag tag){
        this.title = title;
        this.content = content;
        this.user = user;
        this.board = board;
        this.price = price;
        this.studyGroupId = studyGroupId;
        this.tag = tag;
    }

    public void updatePost(PostUpdateRequest request) {
        setTitle(request.getTitle());
        setContent(request.getContent());
        setPhotos(request.getPhotos());
        setPrice(request.getPrice());
        setStudyGroupId(request.getStudyGroupId());
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setPrice(Long price) {
        this.price = price;
    }
    public void setPhotos(List<Photo> photos) {
        this.photos.forEach(photo -> photo.setPost(null));
        this.photos.clear();
        this.photos.addAll(photos);
        this.photos.forEach(photo -> photo.setPost(this));
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }

    public void setStudyGroupId(Long studyGroupId) {
        this.studyGroupId = studyGroupId;
    }

    public void setIsBadgeAccepted(Boolean isBadgeAccepted) {
        this.isBadgeAccepted = isBadgeAccepted;
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
    public void decreaseLikeCount() {
        likeCount--;
    }

    public void increaseReportCount() {
        reportCount++;
    }

    public void decreaseReportCount() {
        reportCount--;
    }

    public Photo getPhoto() {
        if (this.photos.isEmpty()) {
            return null;
        }
        return this.photos.get(0);
    }
}
