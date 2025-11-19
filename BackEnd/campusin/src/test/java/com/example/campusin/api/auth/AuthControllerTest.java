package com.example.campusin.api.auth;

import com.example.campusin.common.config.properties.AppProperties;
import com.example.campusin.common.config.security.SecurityConfig;
import com.example.campusin.domain.auth.AuthReqModel;
import com.example.campusin.domain.oauth.ProviderType;
import com.example.campusin.domain.oauth.RoleType;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.token.AuthToken;
import com.example.campusin.domain.token.AuthTokenProvider;
import com.example.campusin.domain.user.UserRefreshToken;
import com.example.campusin.infra.user.UserRefreshTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController")
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AppProperties appProperties;
    @MockBean
    AuthTokenProvider authTokenProvider;
    @MockBean
    AuthenticationManager authenticationManager;
    @MockBean
    UserRefreshTokenRepository userRefreshTokenRepository;
    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("login 메서드는")
    class Describe_login {

        @Test
        @DisplayName("인증 후 액세스 토큰을 반환한다")
        void returns_access_token() throws Exception {
            // given
            String loginId = "user";
            Long userId = 1L;
            AuthReqModel req = new AuthReqModel();
            req.setLoginId(loginId);
            req.setPassword("pwd");

            UserPrincipal principal = new UserPrincipal(
                    loginId,
                    "pwd",
                    userId,
                    ProviderType.LOCAL,
                    RoleType.USER,
                    List.of()
            );
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
            given(authenticationManager.authenticate(any())).willReturn(authentication);

            AppProperties.Auth authProps = new AppProperties.Auth("secret", 1000L, 2000L);
            given(appProperties.getAuth()).willReturn(authProps);

            AuthToken accessToken = mock(AuthToken.class);
            given(accessToken.getToken()).willReturn("access-token");
            AuthToken refreshToken = mock(AuthToken.class);
            given(refreshToken.getToken()).willReturn("refresh-token");
            given(authTokenProvider.createAuthToken(anyString(), anyString(), anyString(), anyString(), any(Date.class)))
                    .willReturn(accessToken);
            given(authTokenProvider.createAuthToken(anyString(), any(Date.class))).willReturn(refreshToken);
            given(userRefreshTokenRepository.findByLoginId(loginId)).willReturn(null);

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body.token").value("access-token"));
        }
    }

    @Nested
    @DisplayName("refreshToken 메서드는")
    class Describe_refreshToken {

        @Test
        @DisplayName("유효하지 않은 액세스 토큰이면 invalidAccessToken 응답을 반환한다")
        void invalid_access_token() throws Exception {
            // given
            AuthToken invalid = mock(AuthToken.class);
            given(invalid.validate()).willReturn(false);
            given(authTokenProvider.convertAuthToken("bad-token")).willReturn(invalid);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/auth/refresh")
                    .header("Authorization", "Bearer bad-token"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.header.code").value(500))
                    .andExpect(jsonPath("$.header.message").value("Invalid access token."));
        }
    }
}
