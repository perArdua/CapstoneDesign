package com.example.campusin.domain.post.dto.response;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.board.dto.response.BoardSimpleResponse;
import com.example.campusin.domain.photo.response.PhotoResponse;
import com.example.campusin.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Schema(name = "스터디 그룹 기록 페이지 응답")
public class PostStudyResponse {

    @Schema(name = "게시글 id", example = "1")
    private Long postId;

    @Schema(name = "유저 id", example = "1")
    private Long userId;

    @Schema(name = "닉네임", example = "nickname")
    private String nickname;

    @Schema(name = "게시글 제목", example = "title")
    private String title;

    @Schema(name = "게시글 내용", example = "content")
    private String content;

    @Schema(name = "게시글 대표 사진", example = "photo")
    private String photo;

    @Schema(name = "게시글 생성일", example = "2023-05-21 00:00:00")
    private LocalDateTime createdAt;

    @Schema(name = "스터디 그룹 id", example = "1")
    private Long studyGroupId;

    @Builder
    public PostStudyResponse(Long postId, Long userId, String nickname, String title, String content, String photo, LocalDateTime createdAt, Long studyGroupId) {
        this.postId = postId;
        this.userId = userId;
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.photo = photo;
        this.createdAt = createdAt;
        this.studyGroupId = studyGroupId;
    }

    @Builder
    public PostStudyResponse(Post entity){
        this(
                entity.getId(),
                entity.getUser().getId(),
                entity.getUser().getNickname(),
                entity.getTitle(),
                entity.getContent(),
                entity.getPhoto() != null ? entity.getPhoto().getContent() : null,
                entity.getCreatedAt(),
                entity.getStudyGroupId()
        );
    }

}
