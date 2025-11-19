package com.example.campusin.api.rank;

import com.example.campusin.application.rank.RankService;
import com.example.campusin.common.config.security.SecurityConfig;
import com.example.campusin.domain.rank.dto.request.RankCreateRequest;
import com.example.campusin.domain.rank.dto.response.RankIdResponse;
import com.example.campusin.domain.rank.dto.response.RankListQuestResponse;
import com.example.campusin.domain.rank.dto.response.RankListResponse;
import com.example.campusin.domain.rank.dto.response.RankListStudyGroupResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RankController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("RankController")
class RankControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RankService rankService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("createRank 메서드는")
    class Describe_createRank {

        @Test
        @DisplayName("개인 랭킹을 생성한다")
        void creates_rank() throws Exception {
            // given
            Long userId = 1L;
            RankCreateRequest request = new RankCreateRequest(LocalDate.of(2024, 12, 31));
            given(rankService.createRank(eq(userId), any(RankCreateRequest.class)))
                    .willReturn(new RankIdResponse(10L));

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/rank")
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['랭킹 생성'].id").value(10L));
            verify(rankService).createRank(eq(userId), any(RankCreateRequest.class));
        }

        @Test
        @DisplayName("본문이 없어도 서비스 호출 결과를 반환한다")
        void invalid_body() throws Exception {
            // given
            given(rankService.createRank(eq(1L), any(RankCreateRequest.class)))
                    .willReturn(new RankIdResponse(11L));

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/rank")
                    .with(authenticatedUser(1L))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['랭킹 생성'].id").value(11L));
            verify(rankService).createRank(eq(1L), any(RankCreateRequest.class));
        }
    }

    @Nested
    @DisplayName("createStudyGroupRank 메서드는")
    class Describe_createStudyGroupRank {

        @Test
        @DisplayName("스터디 그룹 랭킹을 생성한다")
        void creates_study_group_rank() throws Exception {
            // given
            Long studyGroupId = 5L;
            RankCreateRequest request = new RankCreateRequest(LocalDate.of(2024, 12, 31));
            given(rankService.createStudyRank(eq(studyGroupId), any(RankCreateRequest.class)))
                    .willReturn(new RankIdResponse(20L));

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/rank/{studyGroupId}", studyGroupId)
                    .with(authenticatedUser(2L))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['랭킹 생성'].id").value(20L));
            verify(rankService).createStudyRank(eq(studyGroupId), any(RankCreateRequest.class));
        }
    }

    @Nested
    @DisplayName("getStudyGroupRank 메서드는")
    class Describe_getStudyGroupRank {

        @Test
        @DisplayName("스터디 그룹 랭킹 리스트를 반환한다")
        void returns_study_group_rank() throws Exception {
            // given
            LocalDate date = LocalDate.of(2024, 1, 1);
            PageRequest pageable = PageRequest.of(0, 2);
            RankListStudyGroupResponse item = RankListStudyGroupResponse.builder()
                    .rank(1L)
                    .studyGroupName("스터디")
                    .week(1)
                    .month(1)
                    .build();
            Page<RankListStudyGroupResponse> page = new PageImpl<>(List.of(item), pageable, 1);
            given(rankService.getStudyGroupPersonalStudyTimeRank(eq(date), any(Pageable.class)))
                    .willReturn(page);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/rank/studyGroupRank/")
                    .param("localDate", "2024-01-01")
                    .param("page", "0")
                    .param("size", "2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['랭킹 리스트 조회'].content[0].rank").value(1));
            verify(rankService).getStudyGroupPersonalStudyTimeRank(eq(date), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("getLastWeekStudyGroupRank 메서드는")
    class Describe_getLastWeekStudyGroupRank {

        @Test
        @DisplayName("지난 주 스터디 그룹 랭킹을 반환한다")
        void returns_last_week_study_group_rank() throws Exception {
            // given
            LocalDate date = LocalDate.of(2024, 1, 8);
            PageRequest pageable = PageRequest.of(0, 2);
            RankListStudyGroupResponse item = RankListStudyGroupResponse.builder()
                    .rank(2L)
                    .studyGroupName("스터디2")
                    .week(2)
                    .month(1)
                    .build();
            Page<RankListStudyGroupResponse> page = new PageImpl<>(List.of(item), pageable, 1);
            given(rankService.getStudyGroupPersonalStudyTimeRank(eq(date), any(Pageable.class)))
                    .willReturn(page);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/rank/LastWeek/studyGroupRank/")
                    .param("localDate", "2024-01-08")
                    .param("page", "0")
                    .param("size", "2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['랭킹 리스트 조회'].content[0].rank").value(2));
            verify(rankService).getStudyGroupPersonalStudyTimeRank(eq(date), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("getAllStudyTimeRankList 메서드는")
    class Describe_getAllStudyTimeRankList {

        @Test
        @DisplayName("개인 공부시간 랭킹을 반환한다")
        void returns_study_time_rank() throws Exception {
            // given
            LocalDate date = LocalDate.of(2024, 2, 1);
            PageRequest pageable = PageRequest.of(0, 3);
            RankListResponse item = RankListResponse.builder()
                    .rank(1L)
                    .name("사용자")
                    .week(1)
                    .month(2)
                    .build();
            given(rankService.getAllStudyTimeRankList(eq(date), any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of(item), pageable, 1));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/rank/studyTimeRank")
                    .param("localDate", "2024-02-01")
                    .param("page", "0")
                    .param("size", "3"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['랭킹 리스트 조회'].content[0].rank").value(1));
            verify(rankService).getAllStudyTimeRankList(eq(date), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("getPreviousWeekRankList 메서드는")
    class Describe_getPreviousWeekRankList {

        @Test
        @DisplayName("지난 주 개인 공부시간 랭킹을 반환한다")
        void returns_previous_week_rank() throws Exception {
            // given
            LocalDate date = LocalDate.of(2024, 2, 8);
            PageRequest pageable = PageRequest.of(0, 3);
            RankListResponse item = RankListResponse.builder()
                    .rank(2L)
                    .name("사용자2")
                    .week(2)
                    .month(2)
                    .build();
            given(rankService.getAllStudyTimeRankList(eq(date), any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of(item), pageable, 1));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/rank/LastWeek/studyTimeRank")
                    .param("localDate", "2024-02-08")
                    .param("page", "0")
                    .param("size", "3"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['랭킹 리스트 조회'].content[0].rank").value(2));
            verify(rankService).getAllStudyTimeRankList(eq(date), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("getPreviousWeekRankQuestList 메서드는")
    class Describe_getPreviousWeekRankQuestList {

        @Test
        @DisplayName("지난 주 질의응답 랭킹을 반환한다")
        void returns_previous_week_question_rank() throws Exception {
            // given
            LocalDate date = LocalDate.of(2024, 3, 1);
            PageRequest pageable = PageRequest.of(0, 2);
            RankListQuestResponse item = RankListQuestResponse.builder()
                    .rank(1L)
                    .name("유저")
                    .week(1)
                    .month(3)
                    .build();
            given(rankService.getAllQuestionRankList(eq(date), any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of(item), pageable, 1));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/rank/LastWeek/questionRank")
                    .param("localDate", "2024-03-01")
                    .param("page", "0")
                    .param("size", "2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['랭킹 리스트 조회'].content[0].rank").value(1));
            verify(rankService).getAllQuestionRankList(eq(date), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("getAllQuestionRankList 메서드는")
    class Describe_getAllQuestionRankList {

        @Test
        @DisplayName("질의응답 랭킹을 반환한다")
        void returns_question_rank() throws Exception {
            // given
            LocalDate date = LocalDate.of(2024, 3, 2);
            PageRequest pageable = PageRequest.of(0, 2);
            RankListQuestResponse item = RankListQuestResponse.builder()
                    .rank(2L)
                    .name("유저2")
                    .week(1)
                    .month(3)
                    .build();
            given(rankService.getAllQuestionRankList(eq(date), any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of(item), pageable, 1));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/rank/questionRank")
                    .param("localDate", "2024-03-02")
                    .param("page", "0")
                    .param("size", "2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['랭킹 리스트 조회'].content[0].rank").value(2));
            verify(rankService).getAllQuestionRankList(eq(date), any(Pageable.class));
        }
    }
}
