package com.example.campusin.domain.postsearch;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class PostSearch {

    private String id;
    private String title;
    private String content;
    private Long boardId;
    private Long userId;

    @Builder
    public PostSearch(String id, String title, String content, Long boardId, Long userId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.boardId = boardId;
        this.userId = userId;
    }
}