package com.example.campusin.api.board;

import com.example.campusin.application.post.PostService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.post.dto.request.PostCreateRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Api(tags = {"게시판 API"})
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

    @Operation(summary = "게시글 생성")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 200, message = "게시글 생성 성공, 생성된 게시글의 id 반환"),
    })
    @PostMapping("/{boardId}/posts")
    public ApiResponse createPost(
            @PathVariable(name = "boardId") Long boardId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "파라미터 설명") @RequestBody @Validated PostCreateRequest request
    ) {
        return ApiResponse.success("게시글 생성", postService.createPost(boardId, principal.getUserId(), request));
    }

    @Operation(summary = "게시판 초기화")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 200, message = "게시판 초기화 성공"),
            @io.swagger.annotations.ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("/init")
    public ApiResponse initBoard() {
        return ApiResponse.success("게시판 초기화", postService.initBoard());
    }

    @Operation(summary = "게시판 고유 id값 얻기")
    @GetMapping("/boards/ids")
    public ApiResponse getBoardIds(@PageableDefault(
            sort = {"createdAt"},
            direction = Sort.Direction.DESC
    ) Pageable pageable) {
        return ApiResponse.success("게시판 고유 id값 얻기", postService.getBoardIds(pageable));
    }
}
