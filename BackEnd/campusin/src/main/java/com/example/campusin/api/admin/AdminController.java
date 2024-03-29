package com.example.campusin.api.admin;

import com.example.campusin.application.comment.CommentService;
import com.example.campusin.application.badge.BadgeService;
import com.example.campusin.application.post.PostService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.badge.request.BadgeCreateRequest;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.board.dto.response.BoardSimpleResponse;
import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.comment.dto.response.CommentOneResponse;
import com.example.campusin.domain.comment.dto.response.CommentResponse;
import com.example.campusin.domain.comment.dto.response.CommentsOnPostResponse;
import com.example.campusin.domain.post.dto.response.PostSimpleResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kok8454@gmail.com on 2023-06-05
 * Github : http://github.com/perArdua
 */

@Api(tags = {"관리자 API"})
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PostService postService;
    private final CommentService commentService;
    private final BadgeService badgeService;

    @GetMapping("/badge")
    public ApiResponse showRequestBadge(@PageableDefault(
            sort = {"createdAt"},
            direction = Sort.Direction.DESC
    ) Pageable pageable) {

        Page<BoardSimpleResponse> boardSimpleResponses = postService.getBoardIds(
                Pageable.ofSize(10)
        );

        Long BadgeBoard = boardSimpleResponses.getContent().stream().filter(
                boardSimpleResponse -> boardSimpleResponse.getBoardType().equals(BoardType.AdminBadgeAccept)).map(
                BoardSimpleResponse::getBoardId).findFirst().orElseThrow(IllegalArgumentException::new);

        return ApiResponse.success("뱃지 요청 게시글 목록", postService.getPostsByBoard(BadgeBoard, pageable));
    }

    @GetMapping("/post")
    public ApiResponse showReportedPost(@PageableDefault(
            sort = {"createdAt"},
            direction = Sort.Direction.DESC
    ) Pageable pageable) {

        List<PostSimpleResponse> postSimpleResponses = new ArrayList<>();

        Page<BoardSimpleResponse> boardSimpleResponses = postService.getBoardIds(
                Pageable.ofSize(10)
        );

        for (BoardSimpleResponse boardSimpleResponse : boardSimpleResponses) {
            Page<PostSimpleResponse> postSimpleResponse = postService.getPostsByBoard(boardSimpleResponse.getBoardId(), pageable);
            for (PostSimpleResponse post : postSimpleResponse) {
                if (post.getReportCount() > 0 && !post.getTitle().equals("신고 완료 처리 된 게시글입니다.")) {
                    postSimpleResponses.add(post);
                }
            }
        }

        return ApiResponse.success("게시글 목록", postSimpleResponses);
    }

    @GetMapping("/comment")
    public ApiResponse showReportComment(@PageableDefault(
            sort = {"createdAt"},
            direction = Sort.Direction.DESC
    ) Pageable pageable) {

        Page<Comment> comments = commentService.getAllComments(pageable);
        List<CommentsOnPostResponse> commentsOnPostResponses = new ArrayList<>();

        for (Comment comment : comments) {
            if (comment.getReports().size() > 0 && !comment.getContent().equals("신고 완료 처리 된 댓글입니다.")) {
                commentsOnPostResponses.add(CommentsOnPostResponse.of(comment));
            }
        }

        return ApiResponse.success("댓글 조회 성공", commentsOnPostResponses);
    }

    @GetMapping("/block/post/{postId}")
    public ApiResponse blockPost(@PathVariable Long postId) {
        postService.blockPost(postId);
        return ApiResponse.success("게시글 차단", "게시글 차단 완료");
    }

    @GetMapping("/block/comment/{commentId}")
    public ApiResponse blockComment(@PathVariable Long commentId) {
        commentService.blockComment(commentId);
        return ApiResponse.success("댓글 차단", "댓글 차단 완료");
    }

    @GetMapping("/unblock/post/{postId}")
    public ApiResponse unblockPost(@PathVariable Long postId) {
        postService.unblockPost(postId);
        return ApiResponse.success("게시글 차단 해제", "게시글 차단 해제 완료");
    }

    @GetMapping("/unblock/comment/{commentId}")
    public ApiResponse unblockComment(@PathVariable Long commentId) {
        commentService.unblockComment(commentId);
        return ApiResponse.success("댓글 차단 해제", "댓글 차단 해제 완료");
    }
  
    @Operation(summary = "뱃지 요청 게시글 상태 변경")
    @PutMapping("/badge/{postId}/{isBadgeAccepted}")
    public ApiResponse decideBadgeStatus(@PathVariable Long postId, @PathVariable Boolean isBadgeAccepted) {
        postService.updateBadgeStatus(postId, isBadgeAccepted);
        return ApiResponse.success("뱃지 요청 게시글 상태 변경", "뱃지 요청 게시글 상태 변경 완료");
    }

    @Operation(summary = "뱃지 만들기")
    @PostMapping("/make-badge")
    public ApiResponse makeBadge(@AuthenticationPrincipal UserPrincipal principal, BadgeCreateRequest request) {
        return ApiResponse.success("makeBadge", badgeService.createBadge(principal.getUserId(), request));
    }

}
