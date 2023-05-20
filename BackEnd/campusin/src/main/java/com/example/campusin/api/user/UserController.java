package com.example.campusin.api.user;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

import com.example.campusin.application.user.UserService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse getUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User users = userService.getUser(principal.getUsername());
        return ApiResponse.success("user", users);
    }

    @GetMapping("/nickname")
    public ApiResponse nicknameCheck(@AuthenticationPrincipal UserPrincipal principal) {
        boolean isExist = userService.nicknameCheck(principal.getLoginId());
        return ApiResponse.success("isExist", isExist);
    }

    @PostMapping("/nickname")
    public ApiResponse createNickname(@AuthenticationPrincipal UserPrincipal principal, String nickname) {
        return ApiResponse.success("nickname", userService.createNickname(principal.getLoginId(), nickname));
    }
}
