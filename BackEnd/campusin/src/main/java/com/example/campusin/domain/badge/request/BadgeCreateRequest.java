package com.example.campusin.domain.badge.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kok8454@gmail.com on 2023-06-05
 * Github : http://github.com/perArdua
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(name = "뱃지 생성", description = "뱃지 생성 요청")
public class BadgeCreateRequest {

    @Schema(name = "게시글 id", example = "1")
    private Long postId;

    @Schema(name = "뱃지 이름", example = "뱃지 이름")
    private String name;

    public BadgeCreateRequest(Long postId, String name) {
        this.postId = postId;
        this.name = name;
    }
}
