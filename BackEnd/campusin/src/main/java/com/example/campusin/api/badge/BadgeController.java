package com.example.campusin.api.badge;

import com.example.campusin.application.badge.BadgeService;
import com.example.campusin.application.user.UserService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.badge.request.BadgeCreateRequest;
import com.example.campusin.domain.badge.response.BadgeResponse;
import com.example.campusin.domain.oauth.RoleType;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.user.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Created by kok8454@gmail.com on 2023-06-05
 * Github : http://github.com/perArdua
 */

@Api(tags = {"뱃지 API"})
@RestController
@RequestMapping("/api/v1/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "유저가 갖고 있는 모든 뱃지 읽기 성공", response = BadgeResponse.class, responseContainer = "Page")
            }
    )
    @Operation(summary = "유저가 갖고 있는 모든 뱃지 읽기")
    @GetMapping("/user-badges")
    public ApiResponse getUserBadges(@PathVariable("userId") Long userId,
                                     @PageableDefault(
                                             sort = {"createdAt"},
                                             direction = Sort.Direction.DESC
                                     ) Pageable pageable) {
        return ApiResponse.success("userBadges", badgeService.getBadges(userId, pageable));
    }

}
