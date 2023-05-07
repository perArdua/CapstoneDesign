package com.example.campusin.domain.comment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class CommentResponse {

    private String userId;
    private Long parentId;
    private Long commentId;
    private String name;
    private String content;

    public CommentResponse(String userId, Long parentId, Long commentId, String name, String content) {
        this.userId = userId;
        this.parentId = parentId;
        this.commentId = commentId;
        this.name = name;
        this.content = content;
    }
}
