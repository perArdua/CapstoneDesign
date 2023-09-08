package com.example.campusin.api.comment;

import com.example.campusin.application.comment.CommentReportService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Created by kok8454@gmail.com on 2023-09-09
 * Github : http://github.com/perArdua
 */
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentReportController {

    private final CommentReportService commentReportService;

    @PostMapping("/{commentId}/report")
    public ApiResponse createReport(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId){
        if(commentReportService.createReport(userPrincipal.getUserId(), commentId)){
            return ApiResponse.success("댓글 신고 성공", "Comment Report Created Successfully");
        }
        return ApiResponse.success("이미 신고한 댓글입니다", "Already Reported Comment");
    }

    @DeleteMapping("/{commentId}/report")
    public ApiResponse deleteReport(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId){
        if(commentReportService.deleteReport(userPrincipal.getUserId(), commentId)) {
            return ApiResponse.success("댓글 신고 삭제 성공", "Comment Report Deleted Successfully");
        }
        return ApiResponse.success("신고하지 않은 댓글입니다", "Not Reported Comment");
    }
}
