package com.example.campusin.domain.comment.dto.response;

import com.example.campusin.domain.comment.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(name = "댓글 생성 응답", description = "댓글 생성 응답")
public class CommentCreateResponse {

    @Schema(description = "댓글 id", example = "1")
    private Long commentId;

    @Schema(description = "댓글 내용", example = "댓글 내용")
    private String content;

    @Schema(description = "유저 id", example = "1")
    private Long userId;

    @Schema(description = "유저 닉네임", example = "닉네임")
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
