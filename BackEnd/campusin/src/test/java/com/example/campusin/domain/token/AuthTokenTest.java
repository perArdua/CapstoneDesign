package com.example.campusin.domain.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthToken")
class AuthTokenTest {

    private final Key key = Keys.hmacShaKeyFor("a".repeat(64).getBytes());

    @Test
    @DisplayName("유효한 토큰은 validate가 true이며 Claims를 반환한다")
    void 유효한_토큰() {
        // given
        Date expiry = Date.from(Instant.now().plusSeconds(60));
        AuthToken authToken = new AuthToken("user", expiry, key);

        // when
        boolean result = authToken.validate();
        Claims claims = authToken.getTokenClaims();

        // then
        assertThat(result).isTrue();
        assertThat(claims.getSubject()).isEqualTo("user");
    }

    @Test
    @DisplayName("만료된 토큰은 validate가 false이고 만료 Claims를 조회할 수 있다")
    void 만료된_토큰() {
        // given
        Date expiry = Date.from(Instant.now().minusSeconds(60));
        AuthToken authToken = new AuthToken("user", expiry, key);

        // when
        boolean result = authToken.validate();
        Claims expiredClaims = authToken.getExpiredTokenClaims();

        // then
        assertThat(result).isFalse();
        assertThat(expiredClaims.getSubject()).isEqualTo("user");
    }
}
