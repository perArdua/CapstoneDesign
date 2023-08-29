package com.example.campusin.api.comment;

import com.example.campusin.application.comment.CommentLikeService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping("/{commentId}/like")
    public ResponseEntity<ApiResponse> createLike(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId) throws URISyntaxException {
        commentLikeService.createLike(userPrincipal.getUserId(), commentId);
        URI location = new URI("/api/v1/comments");
        return ResponseEntity.created(location).body(ApiResponse.success("댓글 좋아요 생성 성공", "Comment Like Created Successfully"));
    }

    @DeleteMapping("/{commentId}/like")
    public ApiResponse deleteLike(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId){
        commentLikeService.deleteLike(userPrincipal.getUserId(), commentId);
        return ApiResponse.success("댓글 좋아요 삭제 성공", "Comment Like Deleted Successfully");
    }

}
