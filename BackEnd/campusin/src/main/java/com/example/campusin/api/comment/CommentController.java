package com.example.campusin.api.comment;

import com.example.campusin.application.comment.CommentService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.comment.request.CommentCreateDto;
import com.example.campusin.domain.comment.response.CommentCreateResponse;
import com.example.campusin.domain.loginInfo.OAuth2UserInfo;
import com.example.campusin.domain.oauth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;


@RestController
@RequestMapping("/api/v1/post/{postid}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Validated @RequestBody CommentCreateDto commentCreateDto
            ) throws URISyntaxException {
        CommentCreateResponse response = commentService.createComment(userPrincipal.getLoginId(), commentCreateDto);
        URI location = new URI("/api/v1/post/" + commentCreateDto.getPostId());

        return ResponseEntity.created(location).body(ApiResponse.success("댓글 생성 success", response));
    }


    @DeleteMapping("/{commentId}")
    public ApiResponse deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal UserPrincipal userPrincipal){
        commentService.deleteComment(userPrincipal.getLoginId(), commentId);
        return ApiResponse.success("delete comment", "Comment deleted successfully");
    }

}
