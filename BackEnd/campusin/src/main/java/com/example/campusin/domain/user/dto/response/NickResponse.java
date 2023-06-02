package com.example.campusin.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kok8454@gmail.com on 2023-05-20
 * Github : http://github.com/perArdua
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(name = "닉네임 응답", description = "닉네임")
public class NickResponse {

    @Schema(name = "닉네임", example = "닉네임")
    private String nickname;

    public NickResponse(String nickname) {
        this.nickname = nickname;
    }
}
