package com.example.campusin.api.post;

import com.example.campusin.application.post.PostService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.post.dto.request.PostUpdateRequest;
import com.example.campusin.domain.post.dto.response.PostIdResponse;
import com.example.campusin.domain.post.dto.response.PostResponse;
import com.example.campusin.domain.post.dto.response.PostSimpleResponse;
import com.example.campusin.domain.post.dto.response.PostStudyResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

@Api(tags = {"게시글 API"})
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "게시글 수정 성공", response = PostIdResponse.class)
            }
    )
    @Operation(summary = "게시글 수정")
    @PatchMapping("/{postId}")
    public ApiResponse update(@PathVariable(name = "postId") Long postId, @RequestBody @Validated PostUpdateRequest request) {
        return ApiResponse.success("게시글 수정", postService.updatePost(postId, request));

    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "게시글 읽기 성공", response = PostResponse.class)
            }
    )
    @Operation(summary = "게시글 읽기")
    @GetMapping("/{postId}")
    public ApiResponse showPost(@PathVariable(name = "postId") Long postId) {
        return ApiResponse.success("게시글 상세", postService.readPost(postId));
    }


    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "게시글 삭제 성공")
            }
    )
    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{postId}")
    public ApiResponse delete(@PathVariable(name = "postId") Long postId) {
        postService.deletePost(postId);
        return ApiResponse.success("게시글 삭제", "Post deleted successfully");
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "게시글 검색 성공", response = PostSimpleResponse.class, responseContainer = "Page")
            }
    )
    @Operation(summary = "게시글 검색")
    @GetMapping()
    public ApiResponse searchPosts(@RequestParam String keyword,
                                   @PageableDefault(
                                           sort = {"createdAt"},
                                           direction = Sort.Direction.DESC
                                   ) Pageable pageable) {
        return ApiResponse.success("게시글 검색", postService.searchPosts(keyword, pageable));
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "게시글 목록", response = PostSimpleResponse.class, responseContainer = "Page")
            }
    )
    @Operation(summary = "내가 작성한 게시글 목록")
    @GetMapping("/mypost")
    public ApiResponse showMyPosts(@AuthenticationPrincipal UserPrincipal principal,
                                   @PageableDefault(
                                           sort = {"createdAt"},
                                           direction = Sort.Direction.DESC
                                   ) Pageable pageable) {

        return ApiResponse.success("게시글 목록", postService.getPostsByUser(principal.getUserId(), pageable));
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "내가 작성한 댓글의 게시글 목록", response = PostSimpleResponse.class, responseContainer = "Page")
            }
    )
    @Operation(summary = "내가 작성한 댓글의 게시글 목록")
    @GetMapping("/mycomment")
    public ApiResponse showMyComments(@AuthenticationPrincipal UserPrincipal principal,
                                      @PageableDefault(
                                              sort = {"createdAt"},
                                              direction = Sort.Direction.DESC
                                      ) Pageable pageable) {
        return ApiResponse.success("내가 작성한 댓글의 게시글 목록", postService.getPostsThatUserCommentedAt(principal.getUserId(), pageable));
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "게시글 좋아요")
            }
    )
    @Operation(summary = "게시글 좋아요")
    @PostMapping("/{postId}/like")
    public ApiResponse likePost(@AuthenticationPrincipal UserPrincipal principal,
                                @PathVariable(name = "postId") Long postId) {
        postService.likePost(principal.getUserId(), postId);
        return ApiResponse.success("게시글 좋아요", "Post liked successfully");
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "게시글 좋아요 취소")
            }
    )
    @Operation(summary = "게시글 좋아요 취소")
    @DeleteMapping("/{postId}/like")
    public ApiResponse unlikePost(@AuthenticationPrincipal UserPrincipal principal,
                                  @PathVariable(name = "postId") Long postId) {
        postService.unlikePost(principal.getUserId(), postId);
        return ApiResponse.success("게시글 좋아요 취소", "Post unliked successfully");
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "스터디 그룹 게시글 목록", response = PostStudyResponse.class, responseContainer = "Page")
            }
    )
    @Operation(summary = "스터디 그룹 게시글 목록(스터디원 모두의 게시글)")
    @GetMapping("/{studyGroupId}/posts")
    public ApiResponse getPostsByStudyGroupId(@PathVariable(name = "studyGroupId") Long studyGroupId,
                                              @PageableDefault(
                                                      sort = {"createdAt"},
                                                      direction = Sort.Direction.DESC
                                              ) Pageable pageable) {
        return ApiResponse.success("게시글 목록", postService.getPostsByStudyGroup(studyGroupId, pageable));
    }
}
