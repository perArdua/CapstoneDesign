package com.example.campusin.api.studygroup;

import com.example.campusin.application.studygroup.StudyGroupService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.studygroup.dto.request.StudyGroupCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/studygroup")
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    @PostMapping
    public ApiResponse createStudyGroup(@AuthenticationPrincipal UserPrincipal principal,
                                        @RequestBody @Validated StudyGroupCreateRequest request){
        return ApiResponse.success("StudyGroup 생성이 완료되었습니다.", studyGroupService.createStudyGroup(principal.getUserId(), request));
    }

    @PatchMapping("/{studygroupId}")
    public ApiResponse deleteStudyGroup(@AuthenticationPrincipal UserPrincipal principal,
                                        @RequestBody @Validated StudyGroupCreateRequest request,
                                        @PathVariable("studygroupId") Long studygroupId){
        studyGroupService.deleteStudyGroup(principal.getUserId(), studygroupId);
        return ApiResponse.success("StudyGroup 삭제가 완료되었습니다.", "DELETE TODO SUCCESSFULLY");
    }

    //상세정보
    @GetMapping("/{studygroupId}")
    public ApiResponse showStudyGruop(@AuthenticationPrincipal UserPrincipal principal,
                                      @PathVariable("studygroupId") Long studygroupId){

        return ApiResponse.success("StudyGroup 상세정보 조회가 완료되었습니다.", studyGroupService.showStudyGroup(studygroupId));
    }

    @GetMapping("/mystudygroup")
    public ApiResponse showMyStudyGroupList(@AuthenticationPrincipal UserPrincipal principal,
                                            @PageableDefault(
                                                    sort = {"createdAt"},
                                                    direction = Sort.Direction.DESC
                                            ) Pageable pageable){
        return ApiResponse.success("StudyGroup 상세정보 조회가 완료되었습니다.", studyGroupService.getAllStudyGroupList(principal.getUserId(), pageable));
    }


}
