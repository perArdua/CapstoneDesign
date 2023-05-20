package com.example.campusin.domain.comment.response;

import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class CommentResponse {

    private String userId;
    private Long parentId;
    private Long commentId;
    private String name;
    private String content;

    public CommentResponse(String userId, Long parentId, Long commentId, String name, String content) {
        this.userId = userId;
        this.parentId = parentId;
        this.commentId = commentId;
        this.name = name;
        this.content = content;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CommentCreateResponse {

        private String userId;
        private Long postId;
        private Long parentId;
        private Long commentId;
        private String content;

        @Builder

        public CommentCreateResponse(String userId, Long postId, Long parentId, Long commentId, String content) {
            this.userId = userId;
            this.postId = postId;
            this.parentId = parentId;
            this.commentId = commentId;
            this.content = content;
        }

        public static CommentCreateResponse of(User user, Post post, Comment comment){
            Comment parent = comment.getParent();
            Long parentId = parent == null ? null : parent.getId();

            return CommentCreateResponse.builder()
                    .userId(user.getLoginId())
                    .postId(post.getId())
                    .parentId(parentId)
                    .commentId(comment.getId())
                    .content(comment.getContent())
                    .build();
        }
    }
}
