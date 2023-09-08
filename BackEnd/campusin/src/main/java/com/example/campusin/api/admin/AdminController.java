package com.example.campusin.api.admin;

import com.example.campusin.application.badge.BadgeService;
import com.example.campusin.application.post.PostService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.badge.request.BadgeCreateRequest;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.board.dto.response.BoardSimpleResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
