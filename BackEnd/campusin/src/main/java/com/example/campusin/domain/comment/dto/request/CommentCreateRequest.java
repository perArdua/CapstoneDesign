package com.example.campusin.domain.comment.dto.request;

import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.comment.dto.response.CommentCreateResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@NoArgsConstructor
public class CommentCreateRequest{

    @NotNull
    private Long postId;
    @Schema(name = "부모 댓글 id(대댓글 달성시 parent id 기입)", example = "null")
    private Long parentId;
    @NotBlank
    private  String content;

}
