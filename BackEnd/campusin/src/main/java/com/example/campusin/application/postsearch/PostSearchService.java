package com.example.campusin.application.postsearch;

import com.example.campusin.domain.postsearch.PostSearch;
import com.example.campusin.domain.postsearch.dto.response.PostSearchResponse;
import com.example.campusin.infra.postsearch.PostSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.ActionListener;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostSearchService {
    private final PostSearchRepository postSearchRepository;
    private final PostReindexer postReindexer;

    public void searchWithKeysetPagination(String keyword, String lastSortValue, int size,
                                           ActionListener<List<PostSearchResponse>> listener) {

        postSearchRepository.searchByKeyset(keyword, lastSortValue, size, new ActionListener<>() {
            @Override
            public void onResponse(List<PostSearch> posts) {
                try {
                    List<PostSearchResponse> result = posts.stream()
                            .map(PostSearchResponse::from)
                            .toList();
                    listener.onResponse(result);
                } catch (Exception e) {
                    listener.onFailure(e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });
    }

    public void searchByOffset(
            String keyword,
            int page,
            int size,
            ActionListener<List<PostSearchResponse>> listener
    ) {
        postSearchRepository.searchByOffset(keyword, page, size, new ActionListener<>() {
            @Override
            public void onResponse(List<PostSearch> posts) {
                List<PostSearchResponse> result = posts.stream()
                        .map(PostSearchResponse::from) // DTO 변환
                        .toList();
                listener.onResponse(result);
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });
    }


    @Async
    public void reindexAllAsync() {
        postReindexer.reindexAll();
    }
}
