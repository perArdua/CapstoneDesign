package com.example.campusin.application.postsearch;

import com.example.campusin.domain.postsearch.dto.response.PostSearchResponse;
import com.example.campusin.infra.postsearch.PostSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostSearchService {
    private final PostSearchRepository postSearchRepository;
    private final PostReindexer postReindexer;

    public List<PostSearchResponse> searchWithKeysetPagination(String keyword, String lastSortValue, int size) {
        return postSearchRepository.searchByKeyset(keyword, lastSortValue, size)
                .stream()
                .map(PostSearchResponse::from)
                .toList();
    }

    @Async
    public void reindexAllAsync() {
        postReindexer.reindexAll();
    }
}
