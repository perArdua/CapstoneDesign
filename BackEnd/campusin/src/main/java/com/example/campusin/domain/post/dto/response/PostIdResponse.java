package com.example.campusin.domain.post.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Created by kok8454@gmail.com on 2023-05-08
 * Github : http://github.com/perArdua
 */
@Getter
@Schema(name = "게시글 id 응답", description = "게시글 id를 반환한다.")
public class PostIdResponse {

    @Schema(name = "게시글 id", example = "1")
    private Long postId;
    public PostIdResponse(Long postId) {
        this.postId = postId;
    }
}
