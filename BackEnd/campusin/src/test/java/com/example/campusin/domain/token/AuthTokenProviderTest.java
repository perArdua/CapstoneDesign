package com.example.campusin.domain.token;

import com.example.campusin.application.oauth.CustomUserDetailsService;
import com.example.campusin.application.oauth.exception.TokenValidFailedException;
import com.example.campusin.domain.oauth.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthTokenProvider")
class AuthTokenProviderTest {

    private static final String SECRET = "a".repeat(64);

    @Mock
    CustomUserDetailsService customUserDetailsService;

    AuthTokenProvider authTokenProvider;

    @BeforeEach
    void setUp() {
        authTokenProvider = new AuthTokenProvider(SECRET, customUserDetailsService);
    }

    @Nested
    @DisplayName("getAuthentication 메서드는")
    class Describe_getAuthentication {

        @Test
        @DisplayName("유효한 토큰으로 인증 객체를 생성한다")
        void 인증객체를_생성한다() {
            // given
            Date expiry = Date.from(Instant.now().plusSeconds(300));
            AuthToken authToken = authTokenProvider.createAuthToken("5", "login", "ROLE_USER", "KAKAO", expiry);

            // when
            UsernamePasswordAuthenticationToken authentication =
                    (UsernamePasswordAuthenticationToken) authTokenProvider.getAuthentication(authToken);

            // then
            assertThat(authentication.isAuthenticated()).isTrue();
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            assertThat(principal.getUserId()).isEqualTo(5L);
            assertThat(principal.getProviderType().name()).isEqualTo("KAKAO");
        }

        @Test
        @DisplayName("토큰이 만료되면 TokenValidFailedException을 던진다")
        void 만료된_토큰은_예외() {
            // given
            Date expiry = Date.from(Instant.now().minusSeconds(10));
            AuthToken authToken = authTokenProvider.createAuthToken("5", "login", "ROLE_USER", "KAKAO", expiry);

            // when // then
            assertThatThrownBy(() -> authTokenProvider.getAuthentication(authToken))
                    .isInstanceOf(TokenValidFailedException.class);
        }
    }
}
