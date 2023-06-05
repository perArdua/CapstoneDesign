package com.example.campusin.domain.badge.response;

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
@Schema(name = "뱃지 응답", description = "뱃지")
public class BadgeResponse {

    @Schema(name = "뱃지 id", example = "1")
    private Long badgeId;

    @Schema(name = "뱃지 이름", example = "뱃지 이름")
    private String name;

    public BadgeResponse(Long badgeId, String name) {
        this.badgeId = badgeId;
        this.name = name;
    }
}
