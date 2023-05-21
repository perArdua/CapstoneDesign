package com.example.campusin.domain.post.dto.response;

import com.example.campusin.domain.board.dto.response.BoardSimpleResponse;
import com.example.campusin.domain.post.Post;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiResponse;
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
    Long postId;

    @Schema(name = "게시판 정보")
    BoardSimpleResponse boardSimpleResponse;


    @Schema(name = "닉네임", example = "user1")
    String nickname;

    @Schema(name = "게시글 제목", example = "title")
    String title;

    @Schema(name = "게시글 내용", example = "content")
    String content;

    @Schema(name = "게시글 생성일", example = "2023-05-21 00:00:00")
    private LocalDateTime createdAt;
    @Builder
    public PostSimpleResponse(Long postId, BoardSimpleResponse boardSimpleResponse, String nickname, String title, String content, LocalDateTime createdAt) {
        this.postId = postId;
        this.boardSimpleResponse = boardSimpleResponse;
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    @Builder
    public PostSimpleResponse(Post entity) {
        this(
                entity.getId(),
                new BoardSimpleResponse(entity.getBoard()),
                entity.getUser().getNickname(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreatedAt()
        );

    }
}