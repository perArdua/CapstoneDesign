package com.example.campusin.api.rank;

import com.example.campusin.application.rank.RankService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.rank.dto.request.RankCreateRequest;
import com.example.campusin.domain.rank.dto.response.RankIdResponse;
import com.example.campusin.domain.rank.dto.response.RankListQuestResponse;
import com.example.campusin.domain.rank.dto.response.RankListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@io.swagger.v3.oas.annotations.tags.Tag(name = "RANKING API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rank")
public class RankController {

    private final RankService rankService;

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ranking Id 생성 성공", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RankIdResponse.class)))
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "개인 User Rank Create")
    @PostMapping
    public ApiResponse createRank(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                  @RequestBody RankCreateRequest request ){
        return ApiResponse.success("랭킹 생성", rankService.createRank(userPrincipal.getUserId(), request));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ranking Id 생성 성공", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RankIdResponse.class)))
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "스터디 그룹 Rank Create")
    @PostMapping("/{StudyGroupId}")
    public ApiResponse createStudyGroupRank(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                            @PathVariable Long StudyGroupId,
                                            @RequestBody RankCreateRequest request ){
        return ApiResponse.success("랭킹 생성", rankService.createStudyRank(StudyGroupId, request));
    }
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스터디그룹간 공부시간 랭킹 리스트 조회", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RankListResponse.class)))
            }
    )

    @io.swagger.v3.oas.annotations.Operation(summary = "스터디그룹 공부시간 랭킹 리스트 조회")
    @GetMapping("/studyGroupRank/")
    public ApiResponse getStudyGroupRank(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate,
            @PageableDefault(size = 10) Pageable pageable){
        return ApiResponse.success("랭킹 리스트 조회", rankService.getStudyGroupPersonalStudyTimeRank(localDate, pageable));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스터디그룹간 공부시간 랭킹 리스트 조회", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RankListResponse.class)))
            }
    )

    @io.swagger.v3.oas.annotations.Operation(summary = "이전 주차 스터디그룹 공부시간 랭킹 리스트 조회")
    @GetMapping("/LastWeek/studyGroupRank/")
    public ApiResponse getLastWeekStudyGroupRank(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate,
            @PageableDefault(size = 10) Pageable pageable){
        return ApiResponse.success("랭킹 리스트 조회", rankService.getStudyGroupPersonalStudyTimeRank(localDate, pageable));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공부시간 랭킹 리스트 조회", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RankListResponse.class)))
            }
    )

    @io.swagger.v3.oas.annotations.Operation(summary = "개인 공부시간 랭킹 리스트 조회")
    @GetMapping("/studyTimeRank")
    public ApiResponse getAllStudyTimeRankList(@RequestParam(name = "localDate")
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate localDate,
                                               Pageable pageable){
        return ApiResponse.success("랭킹 리스트 조회", rankService.getAllStudyTimeRankList(localDate, pageable));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이전 주차 랭킹 리스트 조회", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RankListResponse.class)))
            }
    )

    @io.swagger.v3.oas.annotations.Operation(summary = "이전 주차 개인 공부시간 순위 리스트 조회")
    @GetMapping("/LastWeek/studyTimeRank")
    public ApiResponse getPreviousWeekRankList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate,
                                               @PageableDefault(size = 20) Pageable pageable){
        return ApiResponse.success("랭킹 리스트 조회", rankService.getAllStudyTimeRankList(localDate, pageable));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이전 주차 랭킹 리스트 조회", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RankListQuestResponse.class)))
            }
    )

    @io.swagger.v3.oas.annotations.Operation(summary = "이전 주차 개인 질의응답 공부시간 순위 리스트 조회")
    @GetMapping("/LastWeek/questionRank")
    public ApiResponse getPreviousWeekRankQuestList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate,
                                                    @PageableDefault(size = 10) Pageable pageable){
        return ApiResponse.success("랭킹 리스트 조회", rankService.getAllQuestionRankList(localDate, pageable));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "개인 질의응답 랭킹 리스트 조회", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RankListQuestResponse.class)))
            }
    )

    @io.swagger.v3.oas.annotations.Operation(summary = "개인 질의응답 랭킹 리스트 조회")
    @GetMapping("/questionRank")
    public ApiResponse getAllQuestionRankList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate, Pageable pageable){
        return ApiResponse.success("랭킹 리스트 조회", rankService.getAllQuestionRankList(localDate, pageable));
    }
}