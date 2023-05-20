package com.example.campusin.domain.post.dto.request;

import com.example.campusin.domain.photo.Photo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */

@Getter
@NoArgsConstructor
@Schema(name = "게시글 수정 요청", description = "게시글 제목, 게시글 내용, 게시글 사진 전송")
public class PostUpdateRequest {

    @Schema(name = "게시글 제목", description = "게시글 제목", required = true, example = "title")
    @NotBlank
    private String title;

    @Schema(name = "게시글 내용", description = "게시글 내용", required = true, example = "content")
    private String content;

    @Schema(name = "게시글 사진", description = "게시글 사진", required = true, example = "[\"photo1\", \"photo2\", \"photo3\"]")
    private List<Photo> photos;

    @Builder
    public PostUpdateRequest(String title, String content, List<Photo> photos) {
        this.title = title;
        this.content = content;
        this.photos = photos;
        if (Objects.nonNull(photos)) {
            this.photos = photos;
        }
    }
}
