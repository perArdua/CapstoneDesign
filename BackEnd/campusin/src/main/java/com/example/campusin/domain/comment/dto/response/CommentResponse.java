package com.example.campusin.domain.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(name = "댓글 응답", description = "댓글 응답")
public abstract class CommentResponse {

    @Schema(description = "유저 id", example = "1")
    private Long userId;

    @Schema(description = "부모 댓글 id(대댓글 달성시 parent id 기입)", example = "null")
    private Long parentId;

    @Schema(description = "댓글 id", example = "1")
    private Long commentId;

    @Schema(description = "유저 닉네임", example = "닉네임")
    private String name;

    @Schema(description = "댓글 내용", example = "댓글 내용")
    private String content;

    @Schema(description = "좋아요 수", example = "0")
    private Integer like;

    @Schema(description = "신고 횟수", example = "0")
    private Integer report;

    @Schema(description = "채택 여부", example = "false")
    private Boolean isAdopted;

    @Schema(description = "게시판 id", example = "1")
    private Long boardId;

    @Schema(description = "게시글 id", example = "1")
    private Long postId;

    public CommentResponse(Long userId, Long parentId, Long commentId, String name, String content, Integer like, Integer report, Boolean isAdopted, Long boardId, Long postId) {
        this.userId = userId;
        this.parentId = parentId;
        this.commentId = commentId;
        this.name = name;
        this.content = content;
        this.like = like;
        this.report = report;
        this.isAdopted = isAdopted;
        this.boardId = boardId;
        this.postId = postId;
    }
}
