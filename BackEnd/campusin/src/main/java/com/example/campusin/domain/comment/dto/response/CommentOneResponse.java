package com.example.campusin.domain.comment.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public class CommentOneResponse extends CommentResponse{
    @QueryProjection
    public CommentOneResponse(
            Long userId,
            Long parentId,
            Long commentId,
            Integer like,
            String name,
            String content,
            Boolean isAdopted
    ) {
        super(userId, parentId, commentId, name, content, like, isAdopted);
    }
}
