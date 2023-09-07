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
        URI location = new URI("/api/v1/comments");

        if(commentLikeService.createLike(userPrincipal.getUserId(), commentId)){
            return ResponseEntity.created(location).body(ApiResponse.success("댓글 좋아요 생성 성공", "Comment Like Created Successfully"));
        }
        return ResponseEntity.ok(ApiResponse.success("이미 좋아요를 누른 댓글입니다", "Already Liked Comment"));
    }

    @DeleteMapping("/{commentId}/like")
    public ApiResponse deleteLike(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId){
        if(commentLikeService.deleteLike(userPrincipal.getUserId(), commentId)) {
            return ApiResponse.success("댓글 좋아요 삭제 성공", "Comment Like Deleted Successfully");
        }
        return ApiResponse.success("좋아요를 누르지 않은 댓글입니다", "Not Liked Comment");
    }

}
