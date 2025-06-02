package com.example.campusin.api.comment;

import com.example.campusin.application.comment.CommentService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.comment.dto.request.CommentCreateRequest;
import com.example.campusin.domain.comment.dto.response.CommentCreateResponse;
import com.example.campusin.domain.comment.dto.response.CommentsOnPostResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;


@Tag(name = "댓글 API")
@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 생성 성공", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CommentCreateResponse.class))),
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

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 조회 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CommentsOnPostResponse.class)))),
            }
    )
    @Operation(summary = "댓글 조회", description = "댓글을 조회합니다.")
    @GetMapping
    public ApiResponse searchComments(@PathVariable Long postId, Pageable pageable) {
        return ApiResponse.success("댓글 조회 성공", commentService.searchCommentByPost(postId, pageable));
    }

    @Operation(summary = "답변 채택", description = "답변을 채택합니다.")
    @GetMapping("/{commentId}/accept")
    public ApiResponse acceptAnswer(@PathVariable Long commentId, @AuthenticationPrincipal UserPrincipal principal){
        commentService.updateIsAdopted(commentId, principal.getUserId());
        return ApiResponse.success("답변 채택 성공", "ACCEPT ANSWER SUCCESSFULLY");
    }
}
