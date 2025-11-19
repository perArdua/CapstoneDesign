package com.example.campusin.domain.postsearch.dto.response;


import com.example.campusin.domain.postsearch.PostSearch;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostSearchResponse {

    private String id;
    private String title;
    private String content;
    private Long boardId;
    private Long userId;

    @Builder
    public PostSearchResponse(String id, String title, String content, Long boardId, Long userId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.boardId = boardId;
        this.userId = userId;
    }

    public static PostSearchResponse from(PostSearch postSearch) {
        return PostSearchResponse.builder()
                .id(postSearch.getId())
                .title(postSearch.getTitle())
                .content(postSearch.getContent())
                .boardId(postSearch.getBoardId())
                .userId(postSearch.getUserId())
                .build();
    }
}
