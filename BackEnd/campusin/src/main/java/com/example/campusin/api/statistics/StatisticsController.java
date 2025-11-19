package com.example.campusin.api.statistics;

import com.example.campusin.application.statistics.StatisticsService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.statistics.dto.request.StatisticsCreateRequest;
import com.example.campusin.domain.statistics.dto.response.StatisticsIdResponse;
import com.example.campusin.domain.statistics.dto.response.StatisticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "통계 API")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = StatisticsIdResponse.class)))
            }
    )
    @Operation(summary = "통계 생성", description = "특정 날짜에 통계 생성")
    @PostMapping("/create")
    public ApiResponse createStatistics(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                        @RequestBody StatisticsCreateRequest request) {
        return ApiResponse.success("통계 생성", statisticsService.createStatistics(userPrincipal.getUserId(), request));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = StatisticsResponse.class)))
            }
    )
    @Operation(summary = "통계 조회", description = "특정날짜의 통계 조회")
    @GetMapping("/{statisticsId}")
    public ApiResponse readStatistics(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                      @PathVariable Long statisticsId) {
        return ApiResponse.success("통계 조회", statisticsService.readStatistics(userPrincipal.getUserId(), statisticsId));
    }
}