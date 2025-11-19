package com.example.campusin.application.comment;

import com.example.campusin.application.comment.exception.CommentLikeNotFoundException;
import com.example.campusin.application.comment.exception.CommentNotFoundException;
import com.example.campusin.application.user.exception.UserNotFoundException;
import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.comment.CommentLike;
import com.example.campusin.domain.comment.CommentLikeId;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.comment.CommentLikeRepository;
import com.example.campusin.infra.comment.CommentRepository;
import com.example.campusin.infra.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentLikeService")
class CommentLikeServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    CommentLikeRepository commentLikeRepository;
    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentLikeService commentLikeService;

    private User newUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setLoginId("login-" + id);
        return user;
    }

    private Comment newComment(Long id) {
        Comment comment = Comment.builder()
                .user(newUser(10L))
                .post(new Post())
                .content("c")
                .parent(null)
                .isAnswer(false)
                .isAdopted(false)
                .build();
        ReflectionTestUtils.setField(comment, "id", id);
        return comment;
    }

    @Nested
    @DisplayName("createLike 메서드는")
    class Describe_createLike {

        @Test
        @DisplayName("이미 좋아요가 있으면 false를 반환한다")
        void 이미있음() {
            // given
            CommentLikeId id = new CommentLikeId(1L, 2L);
            when(commentLikeRepository.existsById(id)).thenReturn(true);

            // when
            boolean result = commentLikeService.createLike(1L, 2L);

            // then
            assertThat(result).isFalse();
            verifyNoInteractions(userRepository, commentRepository);
        }

        @Test
        @DisplayName("없으면 저장하고 true를 반환한다")
        void 저장한다() {
            // given
            CommentLikeId id = new CommentLikeId(1L, 2L);
            User user = newUser(1L);
            Comment comment = newComment(2L);
            when(commentLikeRepository.existsById(id)).thenReturn(false);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));

            // when
            boolean result = commentLikeService.createLike(1L, 2L);

            // then
            assertThat(result).isTrue();
            verify(commentLikeRepository).save(any(CommentLike.class));
        }

        @Test
        @DisplayName("사용자가 없으면 UserNotFoundException을 던진다")
        void 사용자없음() {
            // given
            CommentLikeId id = new CommentLikeId(1L, 2L);
            when(commentLikeRepository.existsById(id)).thenReturn(false);
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> commentLikeService.createLike(1L, 2L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteLike 메서드는")
    class Describe_deleteLike {

        @Test
        @DisplayName("좋아요가 없으면 false를 반환한다")
        void 없으면_false() {
            // given
            CommentLikeId id = new CommentLikeId(1L, 2L);
            when(commentLikeRepository.existsById(id)).thenReturn(false);

            // when
            boolean result = commentLikeService.deleteLike(1L, 2L);

            // then
            assertThat(result).isFalse();
            verify(commentRepository, never()).findById(any());
        }

        @Test
        @DisplayName("좋아요가 있으면 삭제 후 true를 반환한다")
        void 삭제한다() {
            // given
            CommentLikeId id = new CommentLikeId(1L, 2L);
            Comment comment = newComment(2L);
            comment.getLikes().add(new CommentLike(newUser(1L), comment));
            when(commentLikeRepository.existsById(id)).thenReturn(true);
            when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));
            when(commentLikeRepository.findById(id)).thenReturn(Optional.of(new CommentLike(newUser(1L), comment)));

            // when
            boolean result = commentLikeService.deleteLike(1L, 2L);

            // then
            assertThat(result).isTrue();
            verify(commentLikeRepository).deleteById(id);
        }

        @Test
        @DisplayName("댓글이 없으면 CommentNotFoundException을 던진다")
        void 댓글없음() {
            // given
            CommentLikeId id = new CommentLikeId(1L, 2L);
            when(commentLikeRepository.existsById(id)).thenReturn(true);
            when(commentRepository.findById(2L)).thenReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> commentLikeService.deleteLike(1L, 2L))
                    .isInstanceOf(CommentNotFoundException.class);
        }

        @Test
        @DisplayName("좋아요 엔티티가 없으면 CommentLikeNotFoundException을 던진다")
        void 좋아요엔티티없음() {
            // given
            CommentLikeId id = new CommentLikeId(1L, 2L);
            when(commentLikeRepository.existsById(id)).thenReturn(true);
            when(commentRepository.findById(2L)).thenReturn(Optional.of(newComment(2L)));
            when(commentLikeRepository.findById(id)).thenReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> commentLikeService.deleteLike(1L, 2L))
                    .isInstanceOf(CommentLikeNotFoundException.class);
        }
    }
}
