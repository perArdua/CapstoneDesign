package com.example.campusin.domain.post.dto.response;

import lombok.Getter;

/**
 * Created by kok8454@gmail.com on 2023-05-08
 * Github : http://github.com/perArdua
 */
@Getter
public class PostIdResponse {
    private Long id;

    public PostIdResponse(Long id) {
        this.id = id;
    }
}
