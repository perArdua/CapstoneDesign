package com.example.campusin.domain.comment.dto.response;

import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "댓글 생성 응답", description = "댓글 생성 응답")
public class CommentCreateResponse {

    @ApiModelProperty(value = "댓글 id", example = "1")
    private Long commentId;

    @ApiModelProperty(value = "댓글 내용", example = "댓글 내용")
    private String content;

    @ApiModelProperty(value = "유저 id", example = "1")
    private Long userId;

    @ApiModelProperty(value = "유저 닉네임", example = "닉네임")
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
