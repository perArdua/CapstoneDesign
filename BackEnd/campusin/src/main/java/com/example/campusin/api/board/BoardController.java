package com.example.campusin.api.board;

import com.example.campusin.application.post.PostService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.board.dto.response.BoardSimpleResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.post.dto.request.PostCreateRequest;
import com.example.campusin.domain.post.dto.response.PostIdResponse;
import com.example.campusin.domain.post.dto.response.PostSimpleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "게시판 API")
@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class BoardController {

    private final PostService postService;

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시판별 게시글 목록 조회 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PostSimpleResponse.class))))
            }
    )
    @Operation(summary = "게시판별 게시글 목록 조회")
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

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시판별 게시글 검색 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PostSimpleResponse.class))))
            }
    )
    @Operation(summary = "게시판별 게시글 검색")
    @GetMapping("/{boardId}/posts/search")
    public ApiResponse searchPostsAtBoard(
            @PathVariable(name = "boardId") Long boardId,
            @RequestParam String keyword,
            @PageableDefault(
                    sort = {"createdAt"},
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ApiResponse.success("게시글 목록", postService.searchPostsAtBoard(boardId, keyword, pageable));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 생성 성공, 생성된 게시글의 id 반환", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PostIdResponse.class))),
    })
    @Operation(summary = "게시글 생성")
    @PostMapping("/{boardId}/posts/{tagId}")
    public ApiResponse createPost(
            @PathVariable(name = "boardId") Long boardId,
            @PathVariable(name = "tagId") Long tagId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "파라미터 설명") @RequestBody @Validated PostCreateRequest request
    ) {
        return ApiResponse.success("게시글 생성", postService.createPost(boardId, tagId, principal.getUserId(), request));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시판 초기화 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @Operation(summary = "게시판 초기화 및 태그 초기화")
    @GetMapping("/init")
    public ApiResponse initBoard() {
        return ApiResponse.success("게시판, 태그 초기화", postService.initBoard());
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시판 고유 id값 얻기 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BoardSimpleResponse.class))))
            }
    )
    @Operation(summary = "게시판 고유 id값 얻기")
    @GetMapping("/boards/ids")
    public ApiResponse getBoardIds(@PageableDefault(
            sort = {"createdAt"},
            direction = Sort.Direction.DESC
    ) Pageable pageable) {
        return ApiResponse.success("게시판 고유 id값 얻기", postService.getBoardIds(pageable));
    }

    @Operation(summary = "태그별 고유 id값 얻기")
    @GetMapping("/tags/ids")
    public ApiResponse getTagIds(@PageableDefault(
            sort = {"createdAt"},
            direction = Sort.Direction.DESC
    ) Pageable pageable) {
        return ApiResponse.success("태그별 고유 id값 얻기", postService.getTags(pageable));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "태그별 게시글 목록 조회 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PostSimpleResponse.class))))
            }
    )
    @Operation(summary = "게시판 내 태그별 게시글 목록 조회")
    @GetMapping("tag/{boardId}/{tagId}/posts")
    public ApiResponse showPostsByTag(
            @PathVariable(name = "boardId") Long boardId,
            @PathVariable(name = "tagId") Long tagId,
            @PageableDefault(
                    sort = {"createdAt"},
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ApiResponse.success("게시글 목록", postService.getPostsByTag(boardId, tagId, pageable));
    }
}
