package com.example.campusin.domain.comment.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
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
