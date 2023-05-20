package com.example.campusin.domain.post.dto.response;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.photo.response.PhotoResponse;
import com.example.campusin.domain.post.Post;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */

@Data
@NoArgsConstructor
public class PostResponse {
    private Long postId;
    private BoardType boardType;
    private Long userId;
    private String title;
    private String content;
    private List<PhotoResponse> photoList;


    @Builder
    public PostResponse(Long postId, BoardType boardType, Long userId, String title, String content, List<PhotoResponse> photoList) {
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
                entity.getUser().getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getPhotos().stream().map(PhotoResponse::new).collect(Collectors.toList())
        );
    }
}
