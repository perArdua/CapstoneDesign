package com.example.campusin.common.utils;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CookieUtil")
class CookieUtilTest {

    @Test
    @DisplayName("쿠키 추가/조회/삭제를 수행한다")
    void 쿠키를_추가하고_삭제한다() {
        // given
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        CookieUtil.addCookie(response, "token", "abc", 100);
        request.setCookies(response.getCookies());

        Optional<Cookie> found = CookieUtil.getCookie(request, "token");
        CookieUtil.deleteCookie(request, response, "token");

        // then
        assertThat(found).isPresent();
        Cookie deleted = response.getCookies()[response.getCookies().length - 1];
        assertThat(deleted.getMaxAge()).isZero();
        assertThat(deleted.getValue()).isEmpty();
    }

    @Test
    @DisplayName("직렬화/역직렬화를 수행한다")
    void 직렬화_역직렬화() {
        // given
        String original = "payload";
        Cookie cookie = new Cookie("c", CookieUtil.serialize(original));

        // when
        String deserialized = CookieUtil.deserialize(cookie, String.class);

        // then
        assertThat(deserialized).isEqualTo(original);
    }
}
