package com.example.campusin.application.postsearch;

import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.postsearch.PostSearch;
import com.example.campusin.domain.postsearch.PostSearchMapper;
import com.example.campusin.domain.postsearch.dto.response.PostSearchResponse;
import com.example.campusin.infra.post.PostRepository;
import com.example.campusin.infra.postsearch.PostSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

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
