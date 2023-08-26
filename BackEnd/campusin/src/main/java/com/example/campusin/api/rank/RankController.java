package com.example.campusin.api.rank;

import com.example.campusin.application.rank.RankService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.rank.dto.request.RankCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rank")
public class RankController {

    private final RankService rankService;

    @PostMapping
    public ApiResponse createRank(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                  @RequestBody RankCreateRequest request ){
        return ApiResponse.success("랭킹 생성", rankService.createRank(userPrincipal.getUserId(), request));
    }

//    @PostMapping("/studyGroup/{studyGroupId}")
//    public ApiResponse createStudyGroupRanking(@PathVariable Long studyGroupId,
//                                            @RequestBody RankCreateRequest request ){
//        return ApiResponse.success("스터디 그룹 랭킹 생성", rankService.createStudyGroupRanking(studyGroupId, request));
//    }

    @GetMapping("/studyRank/{rankId}")
    public ApiResponse getPersonalStudyTimeRank(@PathVariable Long rankId,
                                                @AuthenticationPrincipal UserPrincipal userPrincipal){
        return ApiResponse.success("개인 공부시간 랭킹 조회", rankService.getPersonalStudyTimeRank(userPrincipal.getUserId(), rankId));
    }

    @GetMapping("/questionRank/{rankId}")
    public ApiResponse getPersonalQuestionRank(@PathVariable Long rankId,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal){
        return ApiResponse.success("개인 질문 랭킹 조회", rankService.getPersonalQuestionRank(userPrincipal.getUserId(), rankId));
    }

    @GetMapping("/studyGroupRank/{studyGroupId}")
    public ApiResponse getStudyGroupRank(@PathVariable Long rankId,
                                         @AuthenticationPrincipal UserPrincipal userPrincipal){
        return ApiResponse.success("스터디 그룹 랭킹 조회", rankService.getStudyGroupPersonalStudyTimeRank(userPrincipal.getUserId(), rankId));
    }

    @GetMapping("/studyTimeRank")
    public ApiResponse getAllStudyTimeRankList(Pageable pageable){
        return ApiResponse.success("공부시간 랭킹 리스트 조회", rankService.getAllStudyTimeRankList(pageable));
    }

}
