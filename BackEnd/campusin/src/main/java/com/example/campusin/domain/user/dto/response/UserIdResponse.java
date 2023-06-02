package com.example.campusin.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kok8454@gmail.com on 2023-06-02
 * Github : http://github.com/perArdua
 */

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "유저 식별자 반환", description = "유저 식별자 반환")
public class UserIdResponse {

    @Schema(name = "유저 식별자", example = "1")
    private Long userId;

    public UserIdResponse(Long userId) {
        this.userId = userId;
    }
}
