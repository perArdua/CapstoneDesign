package com.example.campusin.infra.user;

import com.example.campusin.domain.user.UserRefreshToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRefreshTokenRepository")
class UserRefreshTokenRepositoryTest {

    @Mock
    UserRefreshTokenRepository repository;

    UserRefreshTokenServiceWrapper wrapper;

    @BeforeEach
    void setUp() {
        wrapper = new UserRefreshTokenServiceWrapper(repository);
    }

    @Test
    @DisplayName("loginId와 refreshToken으로 토큰을 조회한다")
    void 로그인과_리프레시로_조회한다() {
        // given
        String loginId = "user1";
        String refreshToken = "rtoken";
        UserRefreshToken token = new UserRefreshToken(loginId, refreshToken);
        when(repository.findByLoginIdAndRefreshToken(loginId, refreshToken)).thenReturn(token);

        // when
        UserRefreshToken result = wrapper.findByLoginIdAndRefreshToken(loginId, refreshToken);

        // then
        assertThat(result).isEqualTo(token);
    }

    @Test
    @DisplayName("loginId로 토큰을 조회한다")
    void 로그인으로_조회한다() {
        // given
        String loginId = "user2";
        UserRefreshToken token = new UserRefreshToken(loginId, "refresh-2");
        when(repository.findByLoginId(loginId)).thenReturn(token);

        // when
        UserRefreshToken result = wrapper.findByLoginId(loginId);

        // then
        assertThat(result).isEqualTo(token);
    }

    /**
     * Repository 호출을 감싸 테스트하기 위한 얇은 래퍼.
     */
    static class UserRefreshTokenServiceWrapper {
        private final UserRefreshTokenRepository repository;

        UserRefreshTokenServiceWrapper(UserRefreshTokenRepository repository) {
            this.repository = repository;
        }

        UserRefreshToken findByLoginIdAndRefreshToken(String loginId, String refreshToken) {
            return repository.findByLoginIdAndRefreshToken(loginId, refreshToken);
        }

        UserRefreshToken findByLoginId(String loginId) {
            return repository.findByLoginId(loginId);
        }
    }
}
