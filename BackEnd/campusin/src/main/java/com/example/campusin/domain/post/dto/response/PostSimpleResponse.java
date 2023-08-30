package com.example.campusin.domain.post.dto.response;

import com.example.campusin.domain.board.dto.response.BoardSimpleResponse;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.tag.dto.response.TagResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */


@Getter
@Schema(name = "게시글 조회 응답", description = "게시글 id, 게시판 정보, 유저 id, 닉네임, 게시글 제목, 게시글 내용, 게시글 생성일을 반환한다. 게시판 정보는 BoardSimpleResponse로 반환한다.")
public class PostSimpleResponse {

    @Schema(name = "게시글 id", example = "1")
    private Long postId;

    @Schema(name = "게시판 정보")
    private BoardSimpleResponse boardSimpleResponse;

    @Schema(name = "닉네임", example = "user1")
    private String nickname;

    @Schema(name = "게시글 제목", example = "title")
    private String title;

    @Schema(name = "게시글 내용", example = "content")
    private String content;

    @Schema(name = "게시글 생성일", example = "2023-05-21 00:00:00")
    private LocalDateTime createdAt;

    @Schema(name = "책 가격", example = "10000")
    private Long price;

    @Schema(name = "스터디 그룹 id", example = "1")
    private Long studyGroupId;

    @Schema(name = "게시글 대표 사진", example = "photo")
    private String photo;

    @Schema(name = "좋아요 수", example = "1")
    private Integer likeCount;

    @Schema(name = "태그 정보")
    private TagResponse tagResponse;

    @Builder
    public PostSimpleResponse(Long postId, BoardSimpleResponse boardSimpleResponse, String nickname, String title,
                              String content, LocalDateTime createdAt, Long price, Long studyGroupId, String photo,
                              Integer likeCount, TagResponse tagResponse) {
        this.postId = postId;
        this.boardSimpleResponse = boardSimpleResponse;
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.price = price;
        this.studyGroupId = studyGroupId;
        this.photo = photo;
        this.likeCount = likeCount;
        this.tagResponse = tagResponse;
    }

    @Builder
    public PostSimpleResponse(Post entity) {
        this (
                entity.getId(),
                new BoardSimpleResponse(entity.getBoard()),
                entity.getUser().getNickname(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getPrice(),
                entity.getStudyGroupId(),
                entity.getPhoto() != null ? entity.getPhoto().getContent() : null,
                entity.getLikeCount(),
                new TagResponse(entity.getTag())
        );
    }
}