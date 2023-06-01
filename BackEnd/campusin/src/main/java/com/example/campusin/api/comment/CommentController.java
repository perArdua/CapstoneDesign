package com.example.campusin.api.comment;

import com.example.campusin.application.comment.CommentService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.comment.dto.request.CommentCreateRequest;
import com.example.campusin.domain.comment.dto.response.CommentCreateResponse;
import com.example.campusin.domain.comment.dto.response.CommentsOnPostResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;


@Api(tags = {"댓글 API"})
@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "댓글 생성 성공", response = CommentCreateResponse.class),
            }
    )
    @Operation(summary = "댓글 생성", description = "댓글을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse> create(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId,
            @Validated @RequestBody CommentCreateRequest request
    ) throws URISyntaxException {
        CommentCreateResponse response = commentService.createComment(userPrincipal.getUserId(), request, postId);
        URI location = new URI("/api/v1/posts/" + postId);

        return ResponseEntity.created(location).body(ApiResponse.success("댓글 생성 success", response));
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public ApiResponse deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal UserPrincipal principal, @PathVariable Long postId){
        commentService.deleteComment(principal.getUserId(), commentId);
        return ApiResponse.success("댓글 삭제 성공", "COMMENT DELETE SUCCESSFULLY");
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "댓글 조회 성공", response = CommentsOnPostResponse.class, responseContainer = "Page"),
            }
    )
    @Operation(summary = "댓글 조회", description = "댓글을 조회합니다.")
    @GetMapping
    public ApiResponse searchComments(@PathVariable Long postId, Pageable pageable) {
        return ApiResponse.success("댓글 조회 성공", commentService.searchCommentByPost(postId, pageable));
    }

}
