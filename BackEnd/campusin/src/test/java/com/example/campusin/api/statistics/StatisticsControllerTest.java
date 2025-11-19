package com.example.campusin.api.statistics;

import com.example.campusin.application.statistics.StatisticsService;
import com.example.campusin.application.statistics.exception.StatisticsNotFoundException;
import com.example.campusin.application.user.exception.UserNotFoundException;
import com.example.campusin.common.config.security.SecurityConfig;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.statistics.dto.request.StatisticsCreateRequest;
import com.example.campusin.domain.statistics.dto.response.StatisticsIdResponse;
import com.example.campusin.domain.statistics.dto.response.StatisticsResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatisticsController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("StatisticsController")
class StatisticsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    StatisticsService statisticsService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("createStatistics 메서드는")
    class Describe_createStatistics {

        @Test
        @DisplayName("통계를 생성하고 ID를 반환한다")
        void creates_statistics() throws Exception {
            // given
            Long userId = 1L;
            StatisticsCreateRequest request = new StatisticsCreateRequest(LocalDate.of(2024, 1, 1));
            given(statisticsService.createStatistics(eq(userId), any(StatisticsCreateRequest.class)))
                    .willReturn(new StatisticsIdResponse(10L));

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/statistics/create")
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['통계 생성'].id").value(10L));
            verify(statisticsService).createStatistics(eq(userId), any(StatisticsCreateRequest.class));
        }

        @Test
        @DisplayName("사용자가 없으면 404를 반환한다")
        void user_not_found() throws Exception {
            // given
            given(statisticsService.createStatistics(eq(1L), any(StatisticsCreateRequest.class)))
                    .willThrow(new UserNotFoundException());

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/statistics/create")
                    .with(authenticatedUser(1L))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new StatisticsCreateRequest(LocalDate.now()))));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("U001"));
            verify(statisticsService).createStatistics(eq(1L), any(StatisticsCreateRequest.class));
        }
    }

    @Nested
    @DisplayName("readStatistics 메서드는")
    class Describe_readStatistics {

        @Test
        @DisplayName("통계를 조회한다")
        void reads_statistics() throws Exception {
            // given
            Long userId = 2L;
            Long statisticsId = 5L;
            StatisticsResponse response = StatisticsResponse.builder()
                    .totalElapsedTime(1000L)
                    .numberOfQuestions(3L)
                    .numberOfAnswers(4L)
                    .numberOfAdoptedAnswers(1L)
                    .build();
            given(statisticsService.readStatistics(userId, statisticsId)).willReturn(response);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/statistics/{id}", statisticsId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['통계 조회'].totalElapsedTime").value(1000));
            verify(statisticsService).readStatistics(userId, statisticsId);
        }

        @Test
        @DisplayName("통계를 찾지 못하면 404를 반환한다")
        void statistics_not_found() throws Exception {
            // given
            Long userId = 2L;
            Long statisticsId = 99L;
            given(statisticsService.readStatistics(userId, statisticsId))
                    .willThrow(new StatisticsNotFoundException());

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/statistics/{id}", statisticsId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("ST01"));
            verify(statisticsService).readStatistics(userId, statisticsId);
        }
    }
}
