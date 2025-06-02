package com.example.campusin.api.postsearch;

import com.example.campusin.application.postsearch.PostSearchService;
import com.example.campusin.domain.postsearch.dto.response.PostSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post-search")
public class PostSearchController {

    private final PostSearchService postSearchService;

    @GetMapping
    public List<PostSearchResponse> search(
            @RequestParam String keyword,
            @RequestParam(required = false) String lastSortValue,
            @RequestParam(defaultValue = "10") int size) {

        return postSearchService.searchPostsAfter(keyword, lastSortValue, size);
    }

    @GetMapping("/reindex-all")
    public String reindexAll() {
        postSearchService.reindexAllAsync();
        return "재색인 요청 접수 (비동기)";
    }
}
