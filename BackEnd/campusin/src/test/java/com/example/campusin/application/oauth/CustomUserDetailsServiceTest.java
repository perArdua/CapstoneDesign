package com.example.campusin.application.oauth;

import com.example.campusin.domain.oauth.ProviderType;
import com.example.campusin.domain.oauth.RoleType;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService")
class CustomUserDetailsServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    @Nested
    @DisplayName("loadUserByUsername 메서드는")
    class Describe_loadUserByUsername {

        @Test
        @DisplayName("사용자가 없으면 UsernameNotFoundException을 던진다")
        void 사용자_없음() {
            // given
            given(userRepository.findByLoginId("missing")).willReturn(null);

            // when // then
            assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("missing"))
                    .isInstanceOf(UsernameNotFoundException.class);
        }

        @Test
        @DisplayName("사용자를 UserPrincipal로 감싸서 반환한다")
        void 사용자_조회() {
            // given
            User user = new User();
            user.setLoginId("tester");
            user.setUsername("tester");
            user.setProfileImageUrl("img");
            user.setProviderType(ProviderType.GOOGLE);
            user.setRoleType(RoleType.USER);
            given(userRepository.findByLoginId("tester")).willReturn(user);

            // when
            UserPrincipal principal = (UserPrincipal) customUserDetailsService.loadUserByUsername("tester");

            // then
            assertThat(principal.getUsername()).isEqualTo("tester");
            then(userRepository).should().findByLoginId("tester");
        }
    }
}
