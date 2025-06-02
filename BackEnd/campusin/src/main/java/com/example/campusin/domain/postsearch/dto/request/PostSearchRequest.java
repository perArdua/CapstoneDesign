package com.example.campusin.domain.postsearch.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostSearchRequest {

    private String keyword;

    public PostSearchRequest(String keyword) {
        this.keyword = keyword;
    }
}
