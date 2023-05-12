package com.example.campusin.api.board;

import com.example.campusin.application.post.PostService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.post.dto.request.PostCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * Created by kok8454@gmail.com on 2023-05-08
 * Github : http://github.com/perArdua
 */
@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class BoardController {

    private final PostService postService;

    @GetMapping("/{boardId}/posts")
    public ApiResponse showPostsByBoard(
            @PathVariable(name = "boardId") Long boardId,
            @PageableDefault(
                    sort = {"createdAt"},
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ApiResponse.success("게시글 목록", postService.getPostsByBoard(boardId, pageable));
    }
    @GetMapping("/{boardId}/posts/search")
    public ApiResponse searchPostsAtBoard(
            @PathVariable(name = "boardId") Long boardId,
            @RequestParam String keyword,
            @PageableDefault(
                    sort = {"createdAt"},
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ApiResponse.success("게시판별 게시글 목록/검색", postService.searchPostsAtBoard(boardId, keyword, pageable));
    }

    @PostMapping("/{boardId}/posts")
    public ApiResponse createPost(
            @PathVariable(name = "boardId") Long boardId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Validated PostCreateRequest request
    ) {
        return ApiResponse.success("게시글 생성", postService.createPost(boardId, principal.getUserSeq(), request));
    }

    @GetMapping("/init")
    public ApiResponse initBoard() {
        return ApiResponse.success("게시판 초기화", postService.initBoard());
    }
}
