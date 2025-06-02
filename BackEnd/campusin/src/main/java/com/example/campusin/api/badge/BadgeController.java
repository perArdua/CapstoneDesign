package com.example.campusin.api.badge;

import com.example.campusin.application.badge.BadgeService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.badge.response.BadgeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kok8454@gmail.com on 2023-06-05
 * Github : http://github.com/perArdua
 */

@Tag(name = "뱃지 API")
@RestController
@RequestMapping("/api/v1/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "유저가 갖고 있는 모든 뱃지 읽기 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BadgeResponse.class))))
            }
    )
    @Operation(summary = "유저가 갖고 있는 모든 뱃지 읽기")
    @GetMapping("/user-badges/{userId}")
    public ApiResponse getUserBadges(@PathVariable("userId") Long userId,
                                     @PageableDefault(
                                             sort = {"createdAt"},
                                             direction = Sort.Direction.DESC
                                     ) Pageable pageable) {
        return ApiResponse.success("userBadges", badgeService.getBadges(userId, pageable));
    }

}
