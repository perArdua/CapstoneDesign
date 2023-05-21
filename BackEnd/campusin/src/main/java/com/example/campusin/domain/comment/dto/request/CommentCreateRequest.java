package com.example.campusin.domain.comment.dto.request;

import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.comment.dto.response.CommentCreateResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@NoArgsConstructor
@ApiModel(value = "댓글 생성 요청", description = "댓글 생성 요청")
public class CommentCreateRequest{

    @NotNull
    @ApiModelProperty(value = "게시글 id", example = "1")
    private Long postId;

    @ApiModelProperty(value = "부모 댓글 id(대댓글 달성시 parent id 기입)", example = "null")
    private Long parentId;

    @ApiModelProperty(value = "댓글 내용", example = "댓글 내용")
    @NotBlank
    private  String content;

}
