package com.example.campusin.api.postsearch;

import com.example.campusin.application.postsearch.PostSearchService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.postsearch.dto.response.PostSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.ActionListener;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post-search")
public class PostSearchController {

    private final PostSearchService postSearchService;

    @GetMapping("/search")
    public DeferredResult<ResponseEntity<ApiResponse<List<PostSearchResponse>>>> search(
            @RequestParam String keyword,
            @RequestParam(required = false) String lastSortValue,
            @RequestParam(defaultValue = "10") int size
    ) {

        DeferredResult<ResponseEntity<ApiResponse<List<PostSearchResponse>>>> deferredResult = new DeferredResult<>(3000L);

        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ApiResponse.fail()));
        });

        postSearchService.searchWithKeysetPagination(keyword, lastSortValue, size, new ActionListener<>() {
            @Override
            public void onResponse(List<PostSearchResponse> result) {
                deferredResult.setResult(ResponseEntity.ok(ApiResponse.success(result)));
            }

            @Override
            public void onFailure(Exception e) {
                deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.fail()));
            }
        });

        return deferredResult;
    }

    @GetMapping("/search-offset")
    public DeferredResult<ResponseEntity<ApiResponse<List<PostSearchResponse>>>> searchByOffset(
            @RequestParam String keyword,
            @RequestParam int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        DeferredResult<ResponseEntity<ApiResponse<List<PostSearchResponse>>>> deferredResult =
                new DeferredResult<>(3000L);

        // 타임아웃 처리
        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.fail()));
        });

        postSearchService.searchByOffset(keyword, page, size, new ActionListener<>() {
            @Override
            public void onResponse(List<PostSearchResponse> result) {
                deferredResult.setResult(ResponseEntity.ok(ApiResponse.success(result)));
            }

            @Override
            public void onFailure(Exception e) {
                deferredResult.setErrorResult(ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.fail()));
            }
        });

        return deferredResult;
    }


    @PostMapping("/reindex-all")
    public ResponseEntity<ApiResponse<String>> reindexAll() {
        postSearchService.reindexAllAsync();
        return ResponseEntity.accepted().body(ApiResponse.success("재색인 요청이 접수되었습니다."));
    }
}
