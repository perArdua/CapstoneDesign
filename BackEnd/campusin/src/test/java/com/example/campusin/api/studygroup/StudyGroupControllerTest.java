package com.example.campusin.api.studygroup;

import com.example.campusin.application.statistics.StatisticsService;
import com.example.campusin.application.studygroup.StudyGroupService;
import com.example.campusin.application.studygroup.exception.StudyGroupNotFoundException;
import com.example.campusin.common.config.security.SecurityConfig;
import com.example.campusin.domain.studygroup.dto.response.StudyGroupDetailResponse;
import com.example.campusin.domain.studygroup.dto.response.StudyGroupIdResponse;
import com.example.campusin.domain.studygroup.dto.response.StudyGroupResponse;
import com.example.campusin.domain.studygroup.dto.response.StudyGroupTimeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static com.example.campusin.support.MockMvcAuthSupport.authenticatedUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StudyGroupController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("StudyGroupController")
class StudyGroupControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    StudyGroupService studyGroupService;

    @MockBean
    StatisticsService statisticsService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("createStudyGroup 메서드는")
    class Describe_createStudyGroup {

        @Test
        @DisplayName("스터디그룹을 생성한다")
        void creates_studygroup() throws Exception {
            Long userId = 1L;
            String body = """
                    {
                      "studygroupName": "스터디",
                      "limitedMemberSize": 5
                    }
                    """;
            given(studyGroupService.createStudyGroup(eq(userId), any())).willReturn(new StudyGroupIdResponse(10L));

            ResultActions result = mockMvc.perform(post("/api/v1/studygroup")
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['StudyGroup 생성이 완료되었습니다.'].id").value(10));
            verify(studyGroupService).createStudyGroup(eq(userId), any());
        }

        @Test
        @DisplayName("이름이 없으면 400을 반환한다")
        void validation_error() throws Exception {
            String body = """
                    {
                      "limitedMemberSize": 5
                    }
                    """;

            ResultActions result = mockMvc.perform(post("/api/v1/studygroup")
                    .with(authenticatedUser(1L))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("C001"))
                    .andExpect(jsonPath("$.errors[0].field").exists());
            verifyNoInteractions(studyGroupService);
        }
    }

    @Nested
    @DisplayName("joinStudyGroup 메서드는")
    class Describe_joinStudyGroup {

        @Test
        @DisplayName("스터디그룹에 가입한다")
        void joins_studygroup() throws Exception {
            Long userId = 2L;
            String body = """
                    {
                      "studygroupId": 5
                    }
                    """;
            StudyGroupResponse response = StudyGroupResponse.builder()
                    .id(5L)
                    .studygroupName("스터디")
                    .limitedMemberSize(5)
                    .CurrentMemberSize(1)
                    .userName("리더")
                    .build();
            given(studyGroupService.joinStudyGroup(eq(userId), eq(5L))).willReturn(response);

            ResultActions result = mockMvc.perform(post("/api/v1/studygroup/join")
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['StudyGroup 가입이 완료되었습니다.'].id").value(5));
            verify(studyGroupService).joinStudyGroup(userId, 5L);
        }
    }

    @Nested
    @DisplayName("deleteStudyGroup 메서드는")
    class Describe_deleteStudyGroup {

        @Test
        @DisplayName("스터디그룹을 탈퇴/삭제한다")
        void deletes_studygroup() throws Exception {
            Long userId = 3L;
            Long studyGroupId = 7L;

            ResultActions result = mockMvc.perform(delete("/api/v1/studygroup/{studygroupId}", studyGroupId)
                    .with(authenticatedUser(userId)));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['StudyGroup 탈퇴가 완료되었습니다.']").value("DELETE STUDYGROUP SUCCESSFULLY"));
            verify(studyGroupService).deleteStudyGroup(userId, studyGroupId);
        }
    }

    @Nested
    @DisplayName("showStudyGroupInfo 메서드는")
    class Describe_showStudyGroupInfo {

        @Test
        @DisplayName("스터디그룹 상세정보를 반환한다")
        void returns_detail() throws Exception {
            Long studyGroupId = 3L;
            StudyGroupDetailResponse response = StudyGroupDetailResponse.builder()
                    .studyGroupId(studyGroupId)
                    .studyGroupName("스터디")
                    .limitedMemberSize(5)
                    .currentMemberSize(2)
                    .leaderName("리더")
                    .memberList(List.of())
                    .build();
            given(studyGroupService.showStudyGroup(studyGroupId)).willReturn(response);

            ResultActions result = mockMvc.perform(get("/api/v1/studygroup/{studygroupId}", studyGroupId));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['StudyGroup 상세정보 조회가 완료되었습니다.'].studyGroupId").value(studyGroupId));
            verify(studyGroupService).showStudyGroup(studyGroupId);
        }

        @Test
        @DisplayName("스터디그룹이 없으면 404를 반환한다")
        void not_found() throws Exception {
            Long studyGroupId = 99L;
            given(studyGroupService.showStudyGroup(studyGroupId))
                    .willThrow(new StudyGroupNotFoundException());

            ResultActions result = mockMvc.perform(get("/api/v1/studygroup/{studygroupId}", studyGroupId));

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("S001"));
            verify(studyGroupService).showStudyGroup(studyGroupId);
        }
    }

    @Nested
    @DisplayName("showMyStudyGroupList 메서드는")
    class Describe_showMyStudyGroupList {

        @Test
        @DisplayName("내 스터디그룹 목록을 반환한다")
        void returns_my_groups() throws Exception {
            // given
            Long userId = 4L;
            PageRequest pageable = PageRequest.of(0, 2);
            StudyGroupResponse response = StudyGroupResponse.builder()
                    .id(1L)
                    .studygroupName("내스터디")
                    .limitedMemberSize(5)
                    .CurrentMemberSize(3)
                    .userName("나")
                    .build();
            Page<StudyGroupResponse> page = new PageImpl<>(List.of(response), pageable, 1);
            given(studyGroupService.getMyAllStudyGroupList(eq(userId), any(Pageable.class)))
                    .willReturn(page);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/studygroup/mystudygroup")
                    .with(authenticatedUser(userId))
                    .param("page", "0")
                    .param("size", "2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['내가 속한 StudyGroup 목록 조회가 완료되었습니다.'].content[0].id").value(1L));
            verify(studyGroupService).getMyAllStudyGroupList(eq(userId), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("showStudyGroupMemberStudyTime 메서드는")
    class Describe_showStudyGroupMemberStudyTime {

        @Test
        @DisplayName("스터디그룹 멤버들의 주간 공부시간을 반환한다")
        void returns_member_study_time() throws Exception {
            // given
            Long studyGroupId = 5L;
            LocalDate endDate = LocalDate.of(2024, 3, 7);
            PageRequest pageable = PageRequest.of(0, 2);
            StudyGroupTimeResponse resp = new StudyGroupTimeResponse("user", 100L);
            Page<StudyGroupTimeResponse> page = new PageImpl<>(List.of(resp), pageable, 1);
            given(studyGroupService.getStudyGroupMemberStudyTime(eq(studyGroupId), any(LocalDate.class), eq(endDate), any(Pageable.class)))
                    .willReturn(page);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/studygroup/{studyGroupId}/studytime", studyGroupId)
                    .param("endDate", "2024-03-07")
                    .param("page", "0")
                    .param("size", "2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['StudyGroup 멤버들의 주간 공부시간 조회가 완료되었습니다.'].content[0].elapsedTime").value(100));
            verify(studyGroupService).getStudyGroupMemberStudyTime(eq(studyGroupId), any(LocalDate.class), eq(endDate), any(Pageable.class));
        }
    }
}
