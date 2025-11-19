package com.example.campusin.infra.oauth;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OAuth2AuthorizationRequestBasedOnCookieRepository")
class OAuth2AuthorizationRequestBasedOnCookieRepositoryTest {

    OAuth2AuthorizationRequestBasedOnCookieRepository repository =
            new OAuth2AuthorizationRequestBasedOnCookieRepository();

    @Test
    @DisplayName("authorizationRequest가 null이면 관련 쿠키를 모두 삭제한다")
    void 요청이_null이면_쿠키를_삭제한다() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setCookies(
                new Cookie(OAuth2AuthorizationRequestBasedOnCookieRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, "value"),
                new Cookie(OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME, "redirect"),
                new Cookie(OAuth2AuthorizationRequestBasedOnCookieRepository.REFRESH_TOKEN, "rt")
        );

        // when
        repository.saveAuthorizationRequest(null, request, response);

        // then
        assertThat(getCookie(response, OAuth2AuthorizationRequestBasedOnCookieRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME).getMaxAge()).isZero();
        assertThat(getCookie(response, OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME).getMaxAge()).isZero();
        assertThat(getCookie(response, OAuth2AuthorizationRequestBasedOnCookieRepository.REFRESH_TOKEN).getMaxAge()).isZero();
    }

    @Test
    @DisplayName("authorizationRequest가 있으면 직렬화된 쿠키를 저장하고 redirect_uri가 있으면 함께 저장한다")
    void 요청과_리다이렉트를_쿠키에_저장한다() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME, "https://example.com/after");
        MockHttpServletResponse response = new MockHttpServletResponse();

        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri("https://auth")
                .clientId("client")
                .redirectUri("https://example.com/after")
                .state("state")
                .build();

        // when
        repository.saveAuthorizationRequest(authRequest, request, response);

        // then
        assertThat(hasCookie(response, OAuth2AuthorizationRequestBasedOnCookieRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)).isTrue();
        assertThat(hasCookie(response, OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME)).isTrue();
    }

    private Cookie getCookie(MockHttpServletResponse response, String name) {
        return response.getCookies() != null
                ? Arrays.stream(response.getCookies())
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow()
                : null;
    }

    private boolean hasCookie(MockHttpServletResponse response, String name) {
        return response.getCookies() != null
                && Arrays.stream(response.getCookies()).anyMatch(c -> c.getName().equals(name));
    }
}
