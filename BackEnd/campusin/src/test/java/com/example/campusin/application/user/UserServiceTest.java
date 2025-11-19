package com.example.campusin.application.user;

import com.example.campusin.domain.user.User;
import com.example.campusin.domain.user.dto.response.NickResponse;
import com.example.campusin.domain.user.dto.response.UserIdResponse;
import com.example.campusin.infra.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Nested
    @DisplayName("getUser 메서드는")
    class Describe_getUser {

        @Test
        @DisplayName("loginId로 사용자를 조회해 반환한다")
        void 사용자를_반환한다() {
            // given
            User user = new User();
            user.setLoginId("john");
            when(userRepository.findByLoginId("john")).thenReturn(user);

            // when
            User result = userService.getUser("john");

            // then
            assertThat(result).isEqualTo(user);
            verify(userRepository).findByLoginId("john");
            verifyNoMoreInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("nicknameCheck 메서드는")
    class Describe_nicknameCheck {

        @Test
        @DisplayName("닉네임이 없으면 안내 메시지를 반환한다")
        void 닉네임없음() {
            // given
            User user = new User();
            user.setLoginId("john");
            when(userRepository.findByLoginId("john")).thenReturn(user);

            // when
            NickResponse response = userService.nicknameCheck("john");

            // then
            assertThat(response.getNickname()).isEqualTo("해당 닉네임을 설정하지 않았습니다.");
        }

        @Test
        @DisplayName("닉네임이 있으면 해당 닉네임을 반환한다")
        void 닉네임있음() {
            // given
            User user = new User();
            user.setLoginId("john");
            user.setNickname("별명");
            when(userRepository.findByLoginId("john")).thenReturn(user);

            // when
            NickResponse response = userService.nicknameCheck("john");

            // then
            assertThat(response.getNickname()).isEqualTo("별명");
        }
    }

    @Nested
    @DisplayName("createNickname 메서드는")
    class Describe_createNickname {

        @Test
        @DisplayName("닉네임을 설정하고 저장한다")
        void 닉네임을_설정한다() {
            // given
            User user = new User();
            user.setLoginId("amy");
            when(userRepository.findByLoginId("amy")).thenReturn(user);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            User result = userService.createNickname("amy", "별별");

            // then
            assertThat(result.getNickname()).isEqualTo("별별");
            verify(userRepository).save(user);
        }
    }

    @Nested
    @DisplayName("getUserId 메서드는")
    class Describe_getUserId {

        @Test
        @DisplayName("loginId로 조회해 ID를 반환한다")
        void 아이디를_반환한다() {
            // given
            User user = new User();
            user.setLoginId("kim");
            user.setId(7L);
            when(userRepository.findByLoginId("kim")).thenReturn(user);

            // when
            UserIdResponse response = userService.getUserId("kim");

            // then
            assertThat(response.getUserId()).isEqualTo(7L);
        }
    }

    @Nested
    @DisplayName("makeAdmin 메서드는")
    class Describe_makeAdmin {

        @Test
        @DisplayName("사용자를 ADMIN으로 승격하고 저장한다")
        void ADMIN으로_승격한다() {
            // given
            User user = new User();
            user.setLoginId("master");
            when(userRepository.findByLoginId("master")).thenReturn(user);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            User result = userService.makeAdmin("master");

            // then
            assertThat(result.getRoleType()).isNotNull();
            verify(userRepository).save(user);
        }
    }
}
