package com.example.campusin.domain.comment.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "댓글 응답", description = "댓글 응답")
public abstract class CommentResponse {

    @ApiModelProperty(value = "유저 id", example = "1")
    private Long userId;

    @ApiModelProperty(value = "부모 댓글 id(대댓글 달성시 parent id 기입)", example = "null")
    private Long parentId;

    @ApiModelProperty(value = "댓글 id", example = "1")
    private Long commentId;

    @ApiModelProperty(value = "유저 닉네임", example = "닉네임")
    private String name;

    @ApiModelProperty(value = "댓글 내용", example = "댓글 내용")
    private String content;

    @ApiModelProperty(value = "좋아요 수", example = "0")
    private Integer like;

    @ApiModelProperty(value = "신고 횟수", example = "0")
    private Integer report;

    @ApiModelProperty(value = "채택 여부", example = "false")
    private Boolean isAdopted;

    @ApiModelProperty(value = "게시판 id", example = "1")
    private Long boardId;

    @ApiModelProperty(value = "게시글 id", example = "1")
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
