package com.example.campusin.api.user;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

import com.example.campusin.application.user.UserService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.user.User;
import com.example.campusin.domain.user.dto.response.UserIdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용 금지")
    @GetMapping
    public ApiResponse getUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User users = userService.getUser(principal.getUsername());
        return ApiResponse.success("user", users);
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Boolean.class)))
    })
    @Operation(summary = "기존 회원인지 판단", description = "기존 회원이면 true, 아니면 false")
    @GetMapping("/nickname")
    public ApiResponse nicknameCheck(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("기존 회원 닉네임 반환 성공",  userService.nicknameCheck(principal.getLoginId()));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = String.class)))
    })
    @Operation(summary = "닉네임 설정", description = "닉네임 설정 user의 모든 정보 반환")
    @PostMapping("/nickname")
    public ApiResponse createNickname(@AuthenticationPrincipal UserPrincipal principal, String nickname) {
        return ApiResponse.success("nickname", userService.createNickname(principal.getLoginId(), nickname));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserIdResponse.class)))
            }
    )
    @Operation(summary = "유저 고유 식별자 반환", description = "유저 고유 식별자 반환")
    @GetMapping("/id")
    public ApiResponse getUserId(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("userId", userService.getUserId(principal.getLoginId()));
    }

    @Operation(summary = "나를 관리자로 만듦", description = "나를 관리자로 만듦 테스트용임")
    @GetMapping("/make-admin")
    public ApiResponse makeAdmin(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("SUCCESS", userService.makeAdmin(principal.getLoginId()));
    }
}
