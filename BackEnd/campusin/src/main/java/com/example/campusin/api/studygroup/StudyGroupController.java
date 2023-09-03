package com.example.campusin.api.studygroup;

import com.example.campusin.application.statistics.StatisticsService;
import com.example.campusin.application.studygroup.StudyGroupService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.studygroup.dto.request.StudyGroupTimeRequest;
import com.example.campusin.domain.studygroup.dto.request.StudyGroupCreateRequest;
import com.example.campusin.domain.studygroup.dto.request.StudyGroupJoinRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Api(tags = {"스터디그룹 API"})
@RestController
@RequestMapping("/api/v1/studygroup")
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService studyGroupService;
    private final StatisticsService statisticsService;

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "StudyGroup 생성 성공")
            }
    )
    @Operation(summary = "StudyGroup 생성")
    @PostMapping
    public ApiResponse createStudyGroup(@AuthenticationPrincipal UserPrincipal principal,
                                        @RequestBody @Validated StudyGroupCreateRequest request){
        return ApiResponse.success("StudyGroup 생성이 완료되었습니다.", studyGroupService.createStudyGroup(principal.getUserId(), request));
    }

    @Operation(summary = "StudyGroup 가입")
    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "StudyGroup 가입 성공")
            }
    )
    @PostMapping("/join")
    public ApiResponse joinStudyGroup(@AuthenticationPrincipal UserPrincipal principal,
                                      @RequestBody @Validated StudyGroupJoinRequest request){
        return ApiResponse.success("StudyGroup 가입이 완료되었습니다.", studyGroupService.joinStudyGroup(principal.getUserId(), request.getStudygroupId()));
    }

    @Operation(summary = "StudyGroup 탈퇴 및 삭제")
    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "StudyGroup 탈퇴 및 삭제 성공")
            }
    )
    @DeleteMapping("/{studygroupId}")
    public ApiResponse deleteStudyGroup(@AuthenticationPrincipal UserPrincipal principal,
                                        @PathVariable("studygroupId") Long studygroupId){

        studyGroupService.deleteStudyGroup(principal.getUserId(), studygroupId);
        return ApiResponse.success("StudyGroup 탈퇴가 완료되었습니다.", "DELETE STUDYGROUP SUCCESSFULLY");
    }

    //상세정보
    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "StudyGroup 상세정보 조회 성공")
            }
    )
    @Operation(summary = "StudyGroup 상세정보 조회")
    @GetMapping("/{studygroupId}")
    public ApiResponse showStudyGroupInfo(@PathVariable("studygroupId") Long studygroupId){

        return ApiResponse.success("StudyGroup 상세정보 조회가 완료되었습니다.", studyGroupService.showStudyGroup(studygroupId));
    }

    //스터디그룹 목록 조회
    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "StudyGroup 목록 조회 성공")
            }
    )
    @Operation(summary = "StudyGroup 목록 조회")
    @GetMapping("/mystudygroup")
    public ApiResponse showMyStudyGroupList(@AuthenticationPrincipal UserPrincipal principal,
                                            @PageableDefault(
                                                    sort = {"createdAt"},
                                                    direction = Sort.Direction.DESC
                                            ) Pageable pageable){
        return ApiResponse.success("내가 속한 StudyGroup 목록 조회가 완료되었습니다.", studyGroupService.getMyAllStudyGroupList(principal.getUserId(), pageable));
    }

    // 스터디그룹 멤버들의 주간 공부시간 조회
    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "StudyGroup 멤버들의 주간 공부시간 조회 성공")
            }
    )
    @Operation(summary = "StudyGroup 멤버들의 주간 공부시간 조회")
    @GetMapping("/{studyGroupId}/studytime")
    public ApiResponse showStudyGroupMemberStudyTime(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                     @PathVariable Long studyGroupId,
                                                     @PageableDefault(
                                                             sort = {"createdAt"},
                                                             direction = Sort.Direction.DESC
                                                     ) Pageable pageable){

        LocalDate startDate = statisticsService.getStartDate(endDate);
        return ApiResponse.success("StudyGroup 멤버들의 주간 공부시간 조회가 완료되었습니다.", studyGroupService.getStudyGroupMemberStudyTime(studyGroupId, startDate, endDate, pageable));
    }

}
