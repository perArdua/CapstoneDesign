package com.example.campusin.domain.post.dto.response;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.photo.response.PhotoResponse;
import com.example.campusin.domain.post.Post;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */

@Data
@NoArgsConstructor
@Schema(name = "게시글 조회 응답", description = "게시글 ID, 게시판 타입, 유저 id, 닉네임, 게시글 제목, 게시글 내용, 게시글 사진, 게시글 생성일을 반환한다. 게시글 사진은 PhotoResponse로 반환한다.")
public class PostResponse {

    @Schema(name = "게시글 id", example = "1")
    private Long postId;

    @Schema(name = "유저 id", example = "1")
    private Long userId;

    @Schema(name = "게시판 타입", example = "FREE")
    private BoardType boardType;

    @Schema(name = "닉네임", example = "nickname")
    private String nickname;

    @Schema(name = "게시글 제목", example = "title")
    private String title;

    @Schema(name = "게시글 내용", example = "content")
    private String content;

    @Schema(name = "책 가격", example = "10000")
    private Long price;

    @Schema(name = "게시글 사진", example = "[\"photo1\", \"photo2\", \"photo3\"]")
    private List<PhotoResponse> photoList;

    @Schema(name = "게시글 생성일", example = "2023-05-21 00:00:00")
    private LocalDateTime createdAt;

    @Schema(name = "스터디 그룹 id", example = "1")
    private Long studyGroupId;

    @Builder
    public PostResponse(Long postId, Long userId, BoardType boardType, String nickname, String title, String content, List<PhotoResponse> photoList, LocalDateTime createdAt, Long price, Long studyGroupId) {
        this.postId = postId;
        this.userId = userId;
        this.boardType = boardType;
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.photoList = photoList;
        this.createdAt = createdAt;
        this.price = price;
        this.studyGroupId = studyGroupId;
    }

    @Builder
    public PostResponse(Post entity) {
        this(
                entity.getId(),
                entity.getUser().getId(),
                entity.getBoard().getBoardType(),
                entity.getUser().getNickname(),
                entity.getTitle(),
                entity.getContent(),
                entity.getPhotos().stream().map(PhotoResponse::new).collect(Collectors.toList()),
                entity.getCreatedAt(),
                entity.getPrice(),
                entity.getStudyGroupId()
        );
    }
}
