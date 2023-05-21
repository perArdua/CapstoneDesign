package com.example.campusin.domain.post.dto.request;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(name = "게시글 생성 요청", description = "게시글 제목, 게시글 내용, 게시글 사진 전송")
public class PostCreateRequest {

    @Schema(name = "게시글 제목", description = "게시글 제목", required = true, example = "title")
    @NotBlank
    private String title;

    @Schema(name = "게시글 내용", description = "게시글 내용", required = true, example = "content")
    private String content;

    @Schema(name = "게시글 사진", description = "게시글 사진", required = true, example = "[\"photo1\", \"photo2\", \"photo3\"]")
    @NotNull
    private List<String> photos = new ArrayList<>();

    @Builder
    public PostCreateRequest(String title, String content, List<String> photos) {
        this.title = title;
        this.content = content;
        if (Objects.nonNull(photos)) {
            this.photos = photos;
        }
    }
}
