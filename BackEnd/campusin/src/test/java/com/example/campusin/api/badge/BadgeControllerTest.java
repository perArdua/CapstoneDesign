package com.example.campusin.api.badge;

import com.example.campusin.application.badge.BadgeService;
import com.example.campusin.common.config.security.SecurityConfig;
import com.example.campusin.domain.badge.response.BadgeResponse;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BadgeController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("BadgeController")
class BadgeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BadgeService badgeService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("getUserBadges 메서드는")
    class Describe_getUserBadges {

        @Test
        @DisplayName("유저 뱃지 목록을 반환한다")
        void returns_user_badges() throws Exception {
            // given
            Long userId = 1L;
            PageRequest pageable = PageRequest.of(0, 2);
            BadgeResponse badge = new BadgeResponse(10L, "배지");
            Page<BadgeResponse> page = new PageImpl<>(List.of(badge), pageable, 1);
            given(badgeService.getBadges(eq(userId), any(Pageable.class))).willReturn((Page) page);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/badges/user-badges/{userId}", userId)
                    .param("page", "0")
                    .param("size", "2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['userBadges'].content[0].badgeId").value(10L));
        }
    }
}
