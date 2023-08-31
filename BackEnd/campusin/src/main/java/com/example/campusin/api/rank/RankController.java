package com.example.campusin.api.rank;

import com.example.campusin.application.rank.RankService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.rank.dto.request.RankCreateRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"RANKING API"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rank")
public class RankController {

    private final RankService rankService;

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "Ranking Id 생성 성공")
            }
    )
    @Operation(summary = "개인 User Rank Create")
    @PostMapping
    public ApiResponse createRank(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                  @RequestBody RankCreateRequest request ){
        return ApiResponse.success("랭킹 생성", rankService.createRank(userPrincipal.getUserId(), request));
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "Ranking Id 생성 성공")
            }
    )
    @Operation(summary = "스터디 그룹 Rank Create")
    @PostMapping("/{StudyGroupId}")
    public ApiResponse createStudyGroupRank(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                            @PathVariable Long StudyGroupId,
                                            @RequestBody RankCreateRequest request ){
        return ApiResponse.success("스터디 그룹 랭킹 생성", rankService.createStudyRank(userPrincipal.getUserId(), StudyGroupId, request));
    }
    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "스터디그룹내 공부시간 랭킹 리스트 조회")
            }
    )
    @Operation(summary = "스터디그룹 내 공부시간 랭킹 리스트 조회")
    @GetMapping("/studyGroupRank/{studyGroupId}")
    public ApiResponse getStudyGroupRank(
                                         @PathVariable Long studyGroupId,
                                         @AuthenticationPrincipal UserPrincipal userPrincipal,
                                         @PageableDefault(size = 10) Pageable pageable){
        return ApiResponse.success("스터디 그룹 랭킹 조회", rankService.getStudyGroupPersonalStudyTimeRank(studyGroupId, pageable));
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "공부시간 랭킹 리스트 조회")
            }
    )
    @Operation(summary = "개인 공부시간 랭킹 리스트 조회")
    @GetMapping("/studyTimeRank")
    public ApiResponse getAllStudyTimeRankList(Pageable pageable){
        return ApiResponse.success("공부시간 랭킹 리스트 조회", rankService.getAllStudyTimeRankList(pageable));
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "이전 주차 공부시간 랭킹 리스트 조회")
            }
    )
    @Operation(summary = "이전 주차 Rank 공부시간 순위 리스트 조회")
    @GetMapping("/{LastWeek}")
    public ApiResponse getPreviousWeekRankList(@PathVariable int LastWeek,
                                               @PageableDefault(size = 10) Pageable pageable){
        return ApiResponse.success("이전 주 랭킹 리스트 조회", rankService.getAllStudyTimeRankListByWeek(LastWeek, pageable));
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "개인 질의응답 랭킹 리스트 조회")
            }
    )
    @Operation(summary = "개인 질의응답 랭킹 리스트 조회")
    @GetMapping("/questionRank")
    public ApiResponse getAllQuestionRankList(Pageable pageable){
        return ApiResponse.success("개인 질의응답 랭킹 리스트 조회", rankService.getAllQuestionRankList(pageable));
    }

}
