package com.example.campusin.domain.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class CommentResponse {

    private Long userId;

    private Long parentId;

    private Long commentId;

    private String name;
    private String content;

    public CommentResponse(Long userId, Long parentId, Long commentId, String name, String content) {
        this.userId = userId;
        this.parentId = parentId;
        this.commentId = commentId;
        this.name = name;
        this.content = content;
    }
}
