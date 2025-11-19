package com.example.campusin.application.oauth;

import com.example.campusin.application.oauth.exception.OAuthProviderMissMatchException;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomOAuth2UserService")
class CustomOAuth2UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CustomOAuth2UserService customOAuth2UserService;

    @Nested
    @DisplayName("process 메서드는")
    class Describe_process {

        @Test
        @DisplayName("기존 사용자의 provider가 다르면 OAuthProviderMissMatchException을 던진다")
        void 프로바이더_불일치() {
            // given
            OAuth2UserRequest userRequest = createUserRequest("google");
            OAuth2User oAuth2User = createOAuth2User();

            User saved = createUser("login-1", ProviderType.KAKAO, "old", "old");
            given(userRepository.findByLoginId("login-1")).willReturn(saved);

            // when // then
            assertThatThrownBy(() -> invokeProcess(userRequest, oAuth2User))
                    .isInstanceOf(OAuthProviderMissMatchException.class);
        }

        @Test
        @DisplayName("새로운 사용자를 저장하고 UserPrincipal을 반환한다")
        void 새_사용자를_생성한다() {
            // given
            OAuth2UserRequest userRequest = createUserRequest("google");
            OAuth2User oAuth2User = createOAuth2User();

            given(userRepository.findByLoginId("login-1")).willReturn(null);
            given(userRepository.saveAndFlush(any(User.class))).willAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(100L);
                return user;
            });

            // when
            OAuth2User result = invokeProcess(userRequest, oAuth2User);

            // then
            assertThat(result).isInstanceOf(UserPrincipal.class);
            then(userRepository).should().saveAndFlush(any(User.class));
        }

        @Test
        @DisplayName("기존 사용자의 이름과 이미지가 달라지면 갱신한다")
        void 기존_사용자_정보_갱신() {
            // given
            OAuth2UserRequest userRequest = createUserRequest("google");
            OAuth2User oAuth2User = createOAuth2User(Map.of(
                    "sub", "login-1",
                    "name", "newName",
                    "picture", "newImage"
            ));

            User saved = createUser("login-1", ProviderType.GOOGLE, "oldName", "oldImage");
            given(userRepository.findByLoginId("login-1")).willReturn(saved);

            // when
            invokeProcess(userRequest, oAuth2User);

            // then
            assertThat(saved.getUsername()).isEqualTo("newName");
            assertThat(saved.getProfileImageUrl()).isEqualTo("newImage");
            then(userRepository).shouldHaveNoMoreInteractions();
        }
    }

    private OAuth2User invokeProcess(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        return ReflectionTestUtils.invokeMethod(customOAuth2UserService, "process", userRequest, oAuth2User);
    }

    private OAuth2UserRequest createUserRequest(String registrationId) {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(registrationId)
                .clientId("client")
                .clientSecret("secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost")
                .authorizationUri("http://localhost/auth")
                .tokenUri("http://localhost/token")
                .userInfoUri("http://localhost/userinfo")
                .userNameAttributeName("sub")
                .clientName(registrationId)
                .scope("profile")
                .build();

        OAuth2AccessToken token = new OAuth2AccessToken(
                TokenType.BEARER,
                "token",
                Instant.now(),
                Instant.now().plusSeconds(60)
        );
        return new OAuth2UserRequest(clientRegistration, token);
    }

    private OAuth2User createOAuth2User() {
        return createOAuth2User(Map.of(
                "sub", "login-1",
                "name", "name",
                "picture", "image"
        ));
    }

    private OAuth2User createOAuth2User(Map<String, Object> attributes) {
        return new DefaultOAuth2User(
                Set.of(new SimpleGrantedAuthority(RoleType.USER.getCode())),
                attributes,
                "sub"
        );
    }

    private User createUser(String loginId, ProviderType providerType, String name, String image) {
        User user = new User();
        user.setLoginId(loginId);
        user.setProviderType(providerType);
        user.setRoleType(RoleType.USER);
        user.setUsername(name);
        user.setProfileImageUrl(image);
        return user;
    }
}
