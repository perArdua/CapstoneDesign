package com.example.campusin.domain.postsearch;

import com.example.campusin.domain.post.Post;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostSearchMapper {

    public static PostSearch fromPost(Post post) {
        return PostSearch.builder()
                .id(String.valueOf(post.getId()))
                .title(post.getTitle())
                .content(post.getContent())
                .boardId(post.getBoard().getId())
                .userId(post.getUser().getId())
                .build();
    }
}