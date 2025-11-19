package com.example.campusin.api.user;

import com.example.campusin.application.user.UserService;
import com.example.campusin.common.config.security.SecurityConfig;
import com.example.campusin.domain.oauth.ProviderType;
import com.example.campusin.domain.oauth.RoleType;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.user.User;
import com.example.campusin.domain.user.dto.response.NickResponse;
import com.example.campusin.domain.user.dto.response.UserIdResponse;
import com.example.campusin.support.SecurityUserFactory;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

import static com.example.campusin.support.MockMvcAuthSupport.authenticatedUser;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserController")
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("getUser 메서드는")
    class Describe_getUser {

        @Test
        @DisplayName("현재 인증된 사용자를 반환한다")
        void returns_user() throws Exception {
            // given
            User user = new User();
            given(userService.getUser("login")).willReturn(user);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            var securityUser = SecurityUserFactory.securityUser("login", "pwd", List.of("USER"));
            context.setAuthentication(new UsernamePasswordAuthenticationToken(securityUser, "pwd", securityUser.getAuthorities()));
            SecurityContextHolder.setContext(context);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/users"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body.user").exists());
            verify(userService).getUser("login");
        }
    }

    @Nested
    @DisplayName("nicknameCheck 메서드는")
    class Describe_nicknameCheck {

        @Test
        @DisplayName("닉네임 존재 여부를 반환한다")
        void checks_nickname() throws Exception {
            // given
            UserPrincipal principal = testPrincipal(1L, "login");
            given(userService.nicknameCheck(principal.getLoginId())).willReturn(new NickResponse("nick"));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/users/nickname")
                    .with(authenticatedUser(principal)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['기존 회원 닉네임 반환 성공'].nickname").value("nick"));
            verify(userService).nicknameCheck(principal.getLoginId());
        }
    }

    @Nested
    @DisplayName("createNickname 메서드는")
    class Describe_createNickname {

        @Test
        @DisplayName("닉네임을 생성한다")
        void creates_nickname() throws Exception {
            // given
            UserPrincipal principal = testPrincipal(2L, "login2");
            User saved = new User();
            saved.setNickname("nick");
            given(userService.createNickname(anyString(), anyString())).willReturn(saved);

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/users/nickname")
                    .with(authenticatedUser(principal))
                    .param("nickname", "nick"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$['body']['nickname']['nickname']").value("nick"));
            verify(userService).createNickname(principal.getLoginId(), "nick");
        }
    }

    @Nested
    @DisplayName("getUserId 메서드는")
    class Describe_getUserId {

        @Test
        @DisplayName("유저 ID를 반환한다")
        void returns_user_id() throws Exception {
            // given
            UserPrincipal principal = testPrincipal(3L, "login3");
            given(userService.getUserId(principal.getLoginId())).willReturn(new UserIdResponse(99L));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/users/id")
                    .with(authenticatedUser(principal)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$['body']['userId']['userId']").value(99));
            verify(userService).getUserId(principal.getLoginId());
        }
    }

    @Nested
    @DisplayName("makeAdmin 메서드는")
    class Describe_makeAdmin {

        @Test
        @DisplayName("관리자로 승격시킨다")
        void makes_admin() throws Exception {
            // given
            UserPrincipal principal = testPrincipal(4L, "login4");
            User admin = new User();
            given(userService.makeAdmin(principal.getLoginId())).willReturn(admin);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/users/make-admin")
                    .with(authenticatedUser(principal)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body.SUCCESS").exists());
            verify(userService).makeAdmin(principal.getLoginId());
        }
    }

    private UserPrincipal testPrincipal(Long userId, String loginId) {
        return new UserPrincipal(
                loginId,
                "password",
                userId,
                ProviderType.LOCAL,
                RoleType.USER,
                List.of(new SimpleGrantedAuthority(RoleType.USER.getCode()))
        );
    }
}
