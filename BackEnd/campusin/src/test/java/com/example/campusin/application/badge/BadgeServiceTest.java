package com.example.campusin.application.badge;

import com.example.campusin.application.badge.exception.BadgePostNotFoundException;
import com.example.campusin.application.badge.exception.BadgeUnauthorizedException;
import com.example.campusin.application.badge.exception.BadgeUserNotFoundException;
import com.example.campusin.domain.badge.Badge;
import com.example.campusin.domain.badge.request.BadgeCreateRequest;
import com.example.campusin.domain.oauth.ProviderType;
import com.example.campusin.domain.oauth.RoleType;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.badge.BadgeRepository;
import com.example.campusin.infra.post.PostRepository;
import com.example.campusin.infra.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("BadgeService")
class BadgeServiceTest {

    @Mock
    BadgeRepository badgeRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;

    @InjectMocks
    BadgeService badgeService;

    @Nested
    @DisplayName("createBadge 메서드는")
    class Describe_createBadge {

        @Test
        @DisplayName("관리자가 아니면 BadgeUnauthorizedException을 던지고 추가 동작이 없다")
        void 관리자가_아니면_예외() {
            // given
            Long userId = 1L;
            BadgeCreateRequest request = new BadgeCreateRequest(10L, "Top Writer");

            User user = createUser(RoleType.USER);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when // then
            assertThatThrownBy(() -> badgeService.createBadge(userId, request))
                    .isInstanceOf(BadgeUnauthorizedException.class);

            verifyNoInteractions(postRepository);
            verifyNoInteractions(badgeRepository);
        }

        @Test
        @DisplayName("게시글이 없으면 BadgePostNotFoundException을 던진다")
        void 게시글이_없으면_예외() {
            // given
            Long adminId = 2L;
            BadgeCreateRequest request = new BadgeCreateRequest(99L, "Helper");

            User admin = createUser(RoleType.ADMIN);
            given(userRepository.findById(adminId)).willReturn(Optional.of(admin));
            given(postRepository.findById(request.getPostId())).willReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> badgeService.createBadge(adminId, request))
                    .isInstanceOf(BadgePostNotFoundException.class);

            verifyNoInteractions(badgeRepository);
        }

        @Test
        @DisplayName("관리자가 게시글 작성자에게 뱃지를 부여하고 저장 결과를 반환한다")
        void 관리자가_뱃지를_생성한다() {
            // given
            Long adminId = 3L;
            BadgeCreateRequest request = new BadgeCreateRequest(7L, "MVP");

            User admin = createUser(RoleType.ADMIN);
            User postOwner = createUser(RoleType.USER);

            Post post = mock(Post.class);
            given(post.getUser()).willReturn(postOwner);

            given(userRepository.findById(adminId)).willReturn(Optional.of(admin));
            given(postRepository.findById(request.getPostId())).willReturn(Optional.of(post));
            given(badgeRepository.save(any(Badge.class))).willAnswer(invocation -> {
                Badge badge = invocation.getArgument(0);
                ReflectionTestUtils.setField(badge, "id", 55L);
                return badge;
            });

            // when
            Badge result = badgeService.createBadge(adminId, request);

            // then
            assertThat(result.getId()).isEqualTo(55L);
            assertThat(result.getName()).isEqualTo("MVP");
            assertThat(result.getUser()).isEqualTo(postOwner);

            ArgumentCaptor<Badge> badgeCaptor = ArgumentCaptor.forClass(Badge.class);
            then(badgeRepository).should().save(badgeCaptor.capture());
            assertThat(badgeCaptor.getValue().getUser()).isEqualTo(postOwner);
        }
    }

    @Nested
    @DisplayName("getBadges 메서드는")
    class Describe_getBadges {

        @Test
        @DisplayName("사용자가 없으면 BadgeUserNotFoundException을 던진다")
        void 사용자_없음() {
            // given
            Long userId = 8L;
            Pageable pageable = PageRequest.of(0, 10);
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> badgeService.getBadges(userId, pageable))
                    .isInstanceOf(BadgeUserNotFoundException.class);
        }

        @Test
        @DisplayName("사용자의 배지를 페이지 단위로 조회한다")
        void 배지를_조회한다() {
            // given
            Long userId = 9L;
            Pageable pageable = PageRequest.of(0, 3);
            User user = createUser(RoleType.USER);
            Badge badge = Badge.builder().name("Helper").user(user).build();
            Page<Badge> badges = new PageImpl<>(List.of(badge), pageable, 1);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(badgeRepository.findAllByUserId(userId, pageable)).willReturn(badges);

            // when
            Page<Badge> result = badgeService.getBadges(userId, pageable);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Helper");
            then(badgeRepository).should().findAllByUserId(userId, pageable);
        }
    }

    @Nested
    @DisplayName("findUser 메서드는")
    class Describe_findUser {

        @Test
        @DisplayName("사용자가 없으면 BadgeUserNotFoundException을 던진다")
        void 사용자_없음() {
            // given
            Long userId = 100L;
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> badgeService.findUser(userId))
                    .isInstanceOf(BadgeUserNotFoundException.class);
        }

        @Test
        @DisplayName("사용자가 있으면 그대로 반환한다")
        void 사용자_조회() {
            // given
            Long userId = 200L;
            User user = createUser(RoleType.USER);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when
            User found = badgeService.findUser(userId);

            // then
            assertThat(found).isEqualTo(user);
        }
    }

    private User createUser(RoleType roleType) {
        LocalDateTime now = LocalDateTime.now();
        User user = new User("login", "name", "image", ProviderType.GOOGLE, roleType, now, now, "nick");
        user.setId(1L);
        return user;
    }
}
