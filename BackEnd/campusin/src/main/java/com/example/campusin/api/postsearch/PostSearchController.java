package com.example.campusin.api.postsearch;

import com.example.campusin.application.postsearch.PostSearchService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.postsearch.dto.response.PostSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post-search")
public class PostSearchController {

    private final PostSearchService postSearchService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PostSearchResponse>>> search(
            @RequestParam String keyword,
            @RequestParam(required = false) String lastSortValue,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<PostSearchResponse> results = postSearchService.searchWithKeysetPagination(keyword, lastSortValue, size);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @PostMapping("/reindex-all")
    public ResponseEntity<ApiResponse<String>> reindexAll() {
        postSearchService.reindexAllAsync();
        return ResponseEntity.accepted().body(ApiResponse.success("재색인 요청이 접수되었습니다."));
    }
}
