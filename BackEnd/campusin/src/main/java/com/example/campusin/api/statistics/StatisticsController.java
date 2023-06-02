package com.example.campusin.api.statistics;

import com.example.campusin.application.statistics.StatisticsService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.statistics.dto.request.StatisticsCreateRequest;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Created by kok8454@gmail.com on 2023-06-02
 * Github : http://github.com/perArdua
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @PostMapping("/create")
    public ApiResponse createStatistics(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                        @RequestBody StatisticsCreateRequest request) {
        return ApiResponse.success("통계 생성", statisticsService.createStatistics(userPrincipal.getUserId(), request));
    }

    @GetMapping("/{statisticsId}")
    public ApiResponse readStatistics(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                      @PathVariable Long statisticsId) {
        return ApiResponse.success("통계 조회", statisticsService.readStatistics(userPrincipal.getUserId(), statisticsId));
    }
}