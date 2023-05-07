package com.example.campusin.domain.post.dto.response;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.photo.Photo;
import com.example.campusin.domain.post.Post;
import lombok.Builder;

import java.util.List;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */
public class PostResponse {
    private Long postId;
    private BoardType boardType;
    private Long userId;
    private String title;
    private String content;
    private List<Photo> photoList;


    @Builder
    public PostResponse(Long postId, BoardType boardType, Long userId, String title, String content, List<Photo> photoList) {
        this.postId = postId;
        this.boardType = boardType;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.photoList = photoList;
    }

    @Builder
    public PostResponse(Post entity) {
        this(
                entity.getId(),
                entity.getBoard().getBoardType(),
                entity.getUser().getUserSeq(),
                entity.getTitle(),
                entity.getContent(),
                entity.getPhotos()
        );
    }
}
