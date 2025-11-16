package com.example.campusin.api.post;

import com.example.campusin.application.post.PostService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.post.ReportResult;
import com.example.campusin.domain.post.dto.request.PostUpdateRequest;
import com.example.campusin.domain.post.dto.request.ReportRequest;
import com.example.campusin.domain.post.dto.response.PostIdResponse;
import com.example.campusin.domain.post.dto.response.PostResponse;
import com.example.campusin.domain.post.dto.response.PostSimpleResponse;
import com.example.campusin.domain.post.dto.response.PostStudyResponse;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = "게시글 API")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 수정 성공", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PostIdResponse.class)))
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "게시글 수정")
    @PatchMapping("/{postId}")
    public ApiResponse update(@PathVariable(name = "postId") Long postId, @RequestBody @Validated PostUpdateRequest request) {
        return ApiResponse.success("게시글 수정", postService.updatePost(postId, request));

    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 읽기 성공", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PostResponse.class)))
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "게시글 읽기")
    @GetMapping("/{postId}")
    public ApiResponse showPost(@PathVariable(name = "postId") Long postId) {
        return ApiResponse.success("게시글 상세", postService.readPost(postId));
    }


    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 삭제 성공")
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "게시글 삭제")
    @DeleteMapping("/{postId}")
    public ApiResponse delete(@PathVariable(name = "postId") Long postId) {
        postService.deletePost(postId);
        return ApiResponse.success("게시글 삭제", "Post deleted successfully");
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 검색 성공", content = @io.swagger.v3.oas.annotations.media.Content(array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PostSimpleResponse.class))))
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "게시글 검색")
    @GetMapping()
    public ApiResponse searchPosts(@RequestParam String keyword,
                                   @PageableDefault(
                                           sort = {"createdAt"},
                                           direction = Sort.Direction.DESC
                                   ) Pageable pageable) {
        return ApiResponse.success("게시글 검색", postService.searchPosts(keyword, pageable));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 목록", content = @io.swagger.v3.oas.annotations.media.Content(array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PostSimpleResponse.class))))
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "내가 작성한 게시글 목록")
    @GetMapping("/mypost")
    public ApiResponse showMyPosts(@AuthenticationPrincipal UserPrincipal principal,
                                   @PageableDefault(
                                           sort = {"createdAt"},
                                           direction = Sort.Direction.DESC
                                   ) Pageable pageable) {

        return ApiResponse.success("게시글 목록", postService.getPostsByUser(principal.getUserId(), pageable));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내가 작성한 댓글의 게시글 목록", content = @io.swagger.v3.oas.annotations.media.Content(array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PostSimpleResponse.class))))
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "내가 작성한 댓글의 게시글 목록")
    @GetMapping("/mycomment")
    public ApiResponse showMyComments(@AuthenticationPrincipal UserPrincipal principal,
                                      @PageableDefault(
                                              sort = {"createdAt"},
                                              direction = Sort.Direction.DESC
                                      ) Pageable pageable) {
        return ApiResponse.success("내가 작성한 댓글의 게시글 목록", postService.getPostsThatUserCommentedAt(principal.getUserId(), pageable));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 좋아요")
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "게시글 좋아요")
    @PostMapping("/{postId}/like")
    public ApiResponse likePost(@AuthenticationPrincipal UserPrincipal principal,
                                @PathVariable(name = "postId") Long postId) {
        if(postService.likePost(principal.getUserId(), postId)) {
            return ApiResponse.success("게시글 좋아요", "Post liked successfully");
        }
        return ApiResponse.success("이미 좋아요한 게시글입니다.", "Already liked post");
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 좋아요 취소")
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "게시글 좋아요 취소")
    @DeleteMapping("/{postId}/like")
    public ApiResponse unlikePost(@AuthenticationPrincipal UserPrincipal principal,
                                  @PathVariable(name = "postId") Long postId) {
        if(postService.unlikePost(principal.getUserId(), postId)){
            return ApiResponse.success("게시글 좋아요 취소", "Post unliked successfully");
        }
        return ApiResponse.success("좋아요를 누르지 않은 게시글입니다.", "Not liked post");
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 신고")
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "게시글 신고")
    @PostMapping("/{postId}/report")
    public ApiResponse reportPost(@AuthenticationPrincipal UserPrincipal principal,
                                  @PathVariable(name = "postId") Long postId,
                                  @RequestBody ReportRequest request) {

        ReportResult result = postService.reportPost(principal.getUserId(), postId, request.getType());

        return switch (result) {
            case SUCCESS -> ApiResponse.success("게시글 신고", "Post reported successfully");
            case ALREADY_REPORTED -> ApiResponse.success("이미 신고한 게시글입니다.", "Already reported post");
            case POST_HIDDEN -> ApiResponse.success("게시글이 신고로 숨겨졌습니다.", "Post hidden due to reports");
        };
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 신고 취소")
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "게시글 신고 취소")
    @DeleteMapping("/{postId}/report")
    public ApiResponse unreportPost(@AuthenticationPrincipal UserPrincipal principal,
                                    @PathVariable(name = "postId") Long postId) {
        if (postService.unreportPost(principal.getUserId(), postId)) {
            return ApiResponse.success("게시글 신고 취소", "Post unreported successfully");
        }
        return ApiResponse.success("신고하지 않은 게시글입니다.", "Not reported post");
    }
    
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스터디 그룹 게시글 목록", content = @io.swagger.v3.oas.annotations.media.Content(array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PostStudyResponse.class))))
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "스터디 그룹 게시글 목록(스터디원 모두의 게시글)")
    @GetMapping("/{studyGroupId}/posts")
    public ApiResponse getPostsByStudyGroupId(@PathVariable(name = "studyGroupId") Long studyGroupId,
                                              @PageableDefault(
                                                      sort = {"createdAt"},
                                                      direction = Sort.Direction.DESC
                                              ) Pageable pageable) {
        return ApiResponse.success("게시글 목록", postService.getPostsByStudyGroup(studyGroupId, pageable));
    }
}
