package com.example.campusin.api.timer;

import com.example.campusin.application.timer.TimerService;
import com.example.campusin.application.timer.exception.TimerNotFoundException;
import com.example.campusin.common.config.security.SecurityConfig;
import com.example.campusin.domain.timer.request.TimerCreateRequest;
import com.example.campusin.domain.timer.request.TimerUpdateRequest;
import com.example.campusin.domain.timer.response.TimerIdResponse;
import com.example.campusin.domain.timer.response.TimerResponse;
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

import java.time.LocalDateTime;
import java.util.List;

import static com.example.campusin.support.MockMvcAuthSupport.authenticatedUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TimerController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("TimerController")
class TimerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TimerService timerService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("create 메서드는")
    class Describe_create {

        @Test
        @DisplayName("타이머를 생성하고 ID를 반환한다")
        void creates_timer() throws Exception {
            // given
            Long userId = 1L;
            TimerCreateRequest request = TimerCreateRequest.builder()
                    .subject("자료구조")
                    .build();
            given(timerService.createTimer(eq(userId), any(TimerCreateRequest.class)))
                    .willReturn(new TimerIdResponse(10L));

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/timer")
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['Timer 생성이 완료되었습니다.'].id").value(10L));
            verify(timerService).createTimer(eq(userId), any(TimerCreateRequest.class));
        }
    }

    @Nested
    @DisplayName("update 메서드는")
    class Describe_update {

        @Test
        @DisplayName("타이머 시간을 수정한다")
        void updates_timer() throws Exception {
            // given
            Long timerId = 5L;
            TimerUpdateRequest request = new TimerUpdateRequest(100L);
            given(timerService.updateTimer(eq(timerId), any(TimerUpdateRequest.class)))
                    .willReturn(new TimerIdResponse(timerId));

            // when
            ResultActions result = mockMvc.perform(patch("/api/v1/timer/{timerId}", timerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['Timer 수정이 완료되었습니다.'].id").value(5L));
            verify(timerService).updateTimer(eq(timerId), any(TimerUpdateRequest.class));
        }

        @Test
        @DisplayName("타이머가 없으면 404를 반환한다")
        void timer_not_found() throws Exception {
            // given
            Long timerId = 99L;
            given(timerService.updateTimer(eq(timerId), any(TimerUpdateRequest.class)))
                    .willThrow(new TimerNotFoundException());

            // when
            ResultActions result = mockMvc.perform(patch("/api/v1/timer/{timerId}", timerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new TimerUpdateRequest(10L))));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("T001"));
            verify(timerService).updateTimer(eq(timerId), any(TimerUpdateRequest.class));
        }
    }

    @Nested
    @DisplayName("delete 메서드는")
    class Describe_delete {

        @Test
        @DisplayName("타이머를 삭제한다")
        void deletes_timer() throws Exception {
            // given
            Long userId = 2L;
            Long timerId = 6L;

            // when
            ResultActions result = mockMvc.perform(delete("/api/v1/timer/{timerId}", timerId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['Timer 삭제가 완료되었습니다.']").value("DELETE TIMER SUCCESSFULLY"));
            verify(timerService).deleteTimer(userId, timerId);
        }
    }

    @Nested
    @DisplayName("getTimerList 메서드는")
    class Describe_getTimerList {

        @Test
        @DisplayName("타이머 목록을 조회한다")
        void returns_timer_list() throws Exception {
            // given
            Long userId = 3L;
            PageRequest pageable = PageRequest.of(0, 2);
            TimerResponse response = TimerResponse.builder()
                    .id(1L)
                    .subject("과목")
                    .elapsedTime(100L)
                    .userId(userId)
                    .build();
            Page<TimerResponse> page = new PageImpl<>(List.of(response), pageable, 1);
            given(timerService.getAllTimerList(eq(userId), any(Pageable.class))).willReturn(page);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/timer")
                    .with(authenticatedUser(userId))
                    .param("page", "0")
                    .param("size", "2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['Timer 조회가 완료되었습니다.'].content[0].id").value(1L));
            verify(timerService).getAllTimerList(eq(userId), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("getLastDateTime 메서드는")
    class Describe_getLastDateTime {

        @Test
        @DisplayName("마지막 사용 시간을 반환한다")
        void returns_last_time() throws Exception {
            // given
            Long userId = 4L;
            LocalDateTime now = LocalDateTime.now();
            given(timerService.getLastDateTime(userId)).willReturn(now);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/timer/lastDateTime")
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['가장 마지막에 사용한 Timer의 DateTime 조회가 완료되었습니다.']").exists());
            verify(timerService).getLastDateTime(userId);
        }
    }

    @Nested
    @DisplayName("initTimer 메서드는")
    class Describe_initTimer {

        @Test
        @DisplayName("타이머를 초기화한다")
        void inits_timer() throws Exception {
            // given
            Long userId = 5L;
            PageRequest pageable = PageRequest.of(0, 2);
            Page<TimerResponse> page = Page.empty(pageable);
            given(timerService.initTimer(eq(userId), any(Pageable.class))).willReturn(page);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/timer/init")
                    .with(authenticatedUser(userId))
                    .param("page", "0")
                    .param("size", "2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['Timer 초기화가 완료되었습니다.'].content").isArray());
            verify(timerService).initTimer(eq(userId), any(Pageable.class));
        }
    }
}
