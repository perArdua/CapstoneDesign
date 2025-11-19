package com.example.campusin.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("HeaderUtil")
class HeaderUtilTest {

    @Test
    @DisplayName("Bearer prefix가 있을 때 토큰을 반환한다")
    void 토큰을_추출한다() {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        given(request.getHeader("Authorization")).willReturn("Bearer abc.def");

        // when
        String token = HeaderUtil.getAccessToken(request);

        // then
        assertThat(token).isEqualTo("abc.def");
    }

    @Test
    @DisplayName("헤더가 없으면 null을 반환한다")
    void 헤더없음_null() {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        given(request.getHeader("Authorization")).willReturn(null);

        // when
        String token = HeaderUtil.getAccessToken(request);

        // then
        assertThat(token).isNull();
    }
}
