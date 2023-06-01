package com.example.campusin.api.timer;

import com.example.campusin.application.timer.TimerService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.timer.request.TimerCreateRequest;
import com.example.campusin.domain.timer.request.TimerUpdateRequest;
import com.example.campusin.domain.timer.response.TimerIdResponse;
import com.example.campusin.domain.timer.response.TimerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */

@Api(tags = {"타이머 API"})
@RestController
@RequestMapping("/api/v1/timer")
@RequiredArgsConstructor
public class TimerController {

    private final TimerService timerService;

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = TimerIdResponse.class),
            }
    )
    @Operation(summary = "Timer 생성", description = "Timer를 생성합니다.")
    @PostMapping
    public ApiResponse create(@AuthenticationPrincipal UserPrincipal principal,
                       @RequestBody @Validated TimerCreateRequest request) {
        return ApiResponse.success("Timer 생성이 완료되었습니다.", timerService.createTimer(principal.getUserId(), request));
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = TimerIdResponse.class),
            }
    )
    @Operation(summary = "특정 Timer에 시간을 더함", description = "특정 Timer에 시간을 더함 시간은 초 단위임")
    @PatchMapping("/{timerId}")
    public ApiResponse update(@PathVariable(name = "timerId") Long timerId,
                       @RequestBody @Validated TimerUpdateRequest request){
        return ApiResponse.success("Timer 수정이 완료되었습니다.", timerService.updateTimer(timerId, request));
    }

    @Operation(summary = "Timer 삭제", description = "Timer를 삭제합니다.")
    @DeleteMapping("/{timerId}")
    public ApiResponse delete(@AuthenticationPrincipal UserPrincipal principal,
                       @PathVariable("timerId") Long timerId) {
        timerService.deleteTimer(principal.getUserId(),timerId);
        return ApiResponse.success("Timer 삭제가 완료되었습니다.", "DELETE TIMER SUCCESSFULLY");
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = TimerResponse.class, responseContainer = "Page"),
            }
    )
    @Operation(summary = "Timer 조회", description = "Timer를 조회합니다.")
    @GetMapping
    public ApiResponse getTimerList(@AuthenticationPrincipal UserPrincipal principal,
                                    @PageableDefault(
                                            sort = {"createdAt"},
                                            direction = Sort.Direction.DESC
                                    ) final Pageable pageable) {
        return ApiResponse.success("Timer 조회가 완료되었습니다.", timerService.getAllTimerList(principal.getUserId(), pageable));
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = LocalDateTime.class),
            }
    )
    @Operation(summary = "Timer 마지막 시간 조회", description = "Timer 마지막 시간을 조회합니다.")
    @GetMapping("/lastDateTime")
    public ApiResponse getLastDateTime(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("가장 마지막에 사용한 Timer의 DateTime 조회가 완료되었습니다.", timerService.getLastDateTime(principal.getUserId()));
    }
}
