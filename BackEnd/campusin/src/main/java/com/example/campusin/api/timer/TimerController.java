package com.example.campusin.api.timer;

import com.example.campusin.application.timer.TimerService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.timer.request.TimerCreateRequest;
import com.example.campusin.domain.timer.request.TimerUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Created by kok8454@gmail.com on 2023-05-21
 * Github : http://github.com/perArdua
 */

@RestController
@RequestMapping("/api/v1/timer")
@RequiredArgsConstructor
public class TimerController {

    private final TimerService timerService;

    @PostMapping
    public ApiResponse create(@AuthenticationPrincipal UserPrincipal principal,
                       @RequestBody @Validated TimerCreateRequest request) {
        return ApiResponse.success("Timer 생성이 완료되었습니다.", timerService.createTimer(principal.getUserId(), request));
    }

    @PatchMapping("/{timerId}")
    public ApiResponse update(@PathVariable(name = "timerId") Long timerId,
                       @RequestBody @Validated TimerUpdateRequest request){
        return ApiResponse.success("Timer 수정이 완료되었습니다.", timerService.updateTimer(timerId, request));
    }

    @DeleteMapping("/{timerId}")
    public ApiResponse delete(@AuthenticationPrincipal UserPrincipal principal,
                       @PathVariable("timerId") Long timerId) {
        timerService.deleteTimer(principal.getUserId(),timerId);
        return ApiResponse.success("Timer 삭제가 완료되었습니다.", "DELETE TIMER SUCCESSFULLY");
    }

    @GetMapping
    public ApiResponse getTimerList(@AuthenticationPrincipal UserPrincipal principal,
                                    @PageableDefault(
                                            sort = {"createdAt"},
                                            direction = Sort.Direction.DESC
                                    ) final Pageable pageable) {
        return ApiResponse.success("Timer 조회가 완료되었습니다.", timerService.getAllTimerList(principal.getUserId(), pageable));
    }
}
