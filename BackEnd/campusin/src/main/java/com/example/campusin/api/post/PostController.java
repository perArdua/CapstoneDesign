package com.example.campusin.api.post;

import com.example.campusin.application.post.PostService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.post.dto.request.PostUpdateRequest;
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

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PatchMapping("/{postId}")
    public ApiResponse update(@PathVariable(name = "postId") Long postId, @RequestBody @Validated PostUpdateRequest request) {
        return ApiResponse.success("게시글 수정", postService.updatePost(postId, request));

    }

    @GetMapping("/{postId}")
    public ApiResponse showPost(@PathVariable(name = "postId") Long postId) {
        return ApiResponse.success("게시글 상세", postService.readPost(postId));
    }

    @DeleteMapping("/{postId}")
    public ApiResponse delete(@PathVariable(name = "postId") Long postId) {
        postService.deletePost(postId);
        return ApiResponse.success("게시글 삭제", "Post deleted successfully");
    }

    @GetMapping()
    public ApiResponse searchPosts(@RequestParam String keyword,
                                   @PageableDefault(
                                           sort = {"createdAt"},
                                           direction = Sort.Direction.DESC
                                   ) Pageable pageable) {
        return ApiResponse.success("게시글 검색", postService.searchPosts(keyword, pageable));
    }

    @GetMapping("/mypost")
    public ApiResponse showMyPosts(@AuthenticationPrincipal UserPrincipal principal,
                                   @PageableDefault(
                                           sort = {"createdAt"},
                                           direction = Sort.Direction.DESC
                                   ) Pageable pageable) {

        return ApiResponse.success("내가 작성한 게시글 목록", postService.getPostsByUser(principal.getUserId(), pageable));
    }

    @GetMapping("/mycomment")
    public ApiResponse showMyComments(@AuthenticationPrincipal UserPrincipal principal,
                                      @PageableDefault(
                                              sort = {"createdAt"},
                                              direction = Sort.Direction.DESC
                                      ) Pageable pageable) {
        return ApiResponse.success("내가 작성한 댓글의 게시글 목록", postService.getPostsThatUserCommentedAt(principal.getUserId(), pageable));
    }

}
