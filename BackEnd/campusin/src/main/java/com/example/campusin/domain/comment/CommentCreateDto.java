package com.example.campusin.domain.comment;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class CommentCreateDto {

    @NotNull
    private Long postId;
    private Long parentId;

    @NotBlank
    private  String content;

    @Builder
    public CommentCreateDto(Long postId, Long parentId, String content) {
        this.postId = postId;
        this.parentId = parentId;
        this.content = content;
    }
}
