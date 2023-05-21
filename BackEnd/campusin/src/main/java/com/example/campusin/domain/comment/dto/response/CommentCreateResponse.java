package com.example.campusin.domain.comment.dto.response;

import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreateResponse {

    private Long commentId;
    private String content;
    private Long userId;
    private String nickname;

    @Builder
    public CommentCreateResponse(Long userId, String content, Long commentId, String nickname) {
        this.commentId = commentId;
        this.content = content;
        this.userId = userId;
        this.nickname = nickname;
    }

    public static CommentCreateResponse convertComment(Comment comment){
        return comment.getIsDelete() ?
                new CommentCreateResponse(comment.getUser().getId(), "삭제된 댓글 입니다", comment.getId(), comment.getUser().getNickname())
                : new CommentCreateResponse(comment.getUser().getId(), comment.getContent(), comment.getId(), comment.getUser().getNickname());

    }
}
