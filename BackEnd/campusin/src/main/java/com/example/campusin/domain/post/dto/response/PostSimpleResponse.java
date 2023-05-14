package com.example.campusin.domain.post.dto.response;

import com.example.campusin.domain.board.dto.response.BoardSimpleResponse;
import com.example.campusin.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */


@Getter
public class PostSimpleResponse {
    Long postId;
    BoardSimpleResponse boardSimpleResponse;
    Long userId;
    String writer;
    String title;
    String content;


    @Builder
    public PostSimpleResponse(Long postId, BoardSimpleResponse boardSimpleResponse, Long userId, String writer, String title, String content) {
        this.postId = postId;
        this.boardSimpleResponse = boardSimpleResponse;
        this.userId = userId;
        this.writer = writer;
        this.title = title;
        this.content = content;
    }

    @Builder
    public PostSimpleResponse(Post entity) {
        this(
                entity.getId(),
                new BoardSimpleResponse(entity.getBoard()),
                entity.getUser().getId(),
                entity.getUser().getEmail(),
                entity.getTitle(),
                entity.getContent()
        );
    }
}