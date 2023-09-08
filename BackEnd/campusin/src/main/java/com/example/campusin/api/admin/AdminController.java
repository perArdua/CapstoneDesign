package com.example.campusin.api.admin;

import com.example.campusin.application.post.PostService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.board.dto.response.BoardSimpleResponse;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @DeleteMapping("/badge")
    public ApiResponse decideBadgeStatus(Long postId, @PathVariable Boolean isBadgeAccepted) {
        postService.updateBadgeStatus(postId, isBadgeAccepted);
        return ApiResponse.success("뱃지 요청 게시글 상태 변경", "뱃지 요청 게시글 상태 변경 완료");
    }
}
