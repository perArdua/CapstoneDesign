package com.example.campusin.api.timer;

import com.example.campusin.application.timer.TimerService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.timer.request.TimerCreateRequest;
import com.example.campusin.domain.timer.request.TimerUpdateRequest;
import com.example.campusin.domain.timer.response.TimerIdResponse;
import com.example.campusin.domain.timer.response.TimerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "타이머 API")
@RestController
@RequestMapping("/api/v1/timer")
@RequiredArgsConstructor
public class TimerController {

    private final TimerService timerService;

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TimerIdResponse.class))),
            }
    )
    @Operation(summary = "Timer 생성", description = "Timer를 생성합니다.")
    @PostMapping
    public ApiResponse create(@AuthenticationPrincipal UserPrincipal principal,
                       @RequestBody @Validated TimerCreateRequest request) {
        return ApiResponse.success("Timer 생성이 완료되었습니다.", timerService.createTimer(principal.getUserId(), request));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TimerIdResponse.class))),
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

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TimerResponse.class)))),
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

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LocalDateTime.class))),
            }
    )
    @Operation(summary = "Timer 마지막 시간 조회", description = "Timer 마지막 시간을 조회합니다.")
    @GetMapping("/lastDateTime")
    public ApiResponse getLastDateTime(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("가장 마지막에 사용한 Timer의 DateTime 조회가 완료되었습니다.", timerService.getLastDateTime(principal.getUserId()));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK")
            }
    )
    @Operation(summary = "Timer 초기화", description = "Timer를 초기화합니다.")
    @GetMapping("/init")
    public ApiResponse initTimer(@AuthenticationPrincipal UserPrincipal principal,
                                @PageableDefault(
                                        sort = {"createdAt"},
                                        direction = Sort.Direction.DESC
                                ) final Pageable pageable) {
        return ApiResponse.success("Timer 초기화가 완료되었습니다.", timerService.initTimer(principal.getUserId(), pageable));
    }
}
