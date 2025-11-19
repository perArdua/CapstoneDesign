package com.example.campusin.application.comment;

import com.example.campusin.application.comment.exception.CommentNotFoundException;
import com.example.campusin.application.comment.exception.CommentReportNotFoundException;
import com.example.campusin.application.user.exception.UserNotFoundException;
import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.comment.CommentReport;
import com.example.campusin.domain.comment.CommentReportId;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.comment.CommentReportRepository;
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
@DisplayName("CommentReportService")
class CommentReportServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    CommentReportRepository commentReportRepository;
    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentReportService commentReportService;

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
    @DisplayName("createReport 메서드는")
    class Describe_createReport {

        @Test
        @DisplayName("이미 신고가 있으면 false를 반환한다")
        void 이미신고() {
            // given
            CommentReportId id = new CommentReportId(1L, 2L);
            when(commentReportRepository.existsById(id)).thenReturn(true);

            // when
            boolean result = commentReportService.createReport(1L, 2L);

            // then
            assertThat(result).isFalse();
            verifyNoInteractions(userRepository, commentRepository);
        }

        @Test
        @DisplayName("없으면 신고를 저장하고 true를 반환한다")
        void 신고한다() {
            // given
            CommentReportId id = new CommentReportId(1L, 2L);
            User user = newUser(1L);
            Comment comment = newComment(2L);
            when(commentReportRepository.existsById(id)).thenReturn(false);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));

            // when
            boolean result = commentReportService.createReport(1L, 2L);

            // then
            assertThat(result).isTrue();
            verify(commentReportRepository).save(any(CommentReport.class));
        }

        @Test
        @DisplayName("사용자가 없으면 UserNotFoundException을 던진다")
        void 사용자없음() {
            // given
            CommentReportId id = new CommentReportId(1L, 2L);
            when(commentReportRepository.existsById(id)).thenReturn(false);
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> commentReportService.createReport(1L, 2L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteReport 메서드는")
    class Describe_deleteReport {

        @Test
        @DisplayName("신고가 없으면 false를 반환한다")
        void 신고없음() {
            // given
            CommentReportId id = new CommentReportId(1L, 2L);
            when(commentReportRepository.existsById(id)).thenReturn(false);

            // when
            boolean result = commentReportService.deleteReport(1L, 2L);

            // then
            assertThat(result).isFalse();
            verify(commentRepository, never()).findById(any());
        }

        @Test
        @DisplayName("신고가 있으면 삭제하고 true를 반환한다")
        void 삭제한다() {
            // given
            CommentReportId id = new CommentReportId(1L, 2L);
            Comment comment = newComment(2L);
            comment.getReports().add(new CommentReport(newUser(1L), comment));
            when(commentReportRepository.existsById(id)).thenReturn(true);
            when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));
            when(commentReportRepository.findById(id)).thenReturn(Optional.of(new CommentReport(newUser(1L), comment)));

            // when
            boolean result = commentReportService.deleteReport(1L, 2L);

            // then
            assertThat(result).isTrue();
            verify(commentReportRepository).deleteById(id);
        }

        @Test
        @DisplayName("댓글이 없으면 CommentNotFoundException을 던진다")
        void 댓글없음() {
            // given
            CommentReportId id = new CommentReportId(1L, 2L);
            when(commentReportRepository.existsById(id)).thenReturn(true);
            when(commentRepository.findById(2L)).thenReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> commentReportService.deleteReport(1L, 2L))
                    .isInstanceOf(CommentNotFoundException.class);
        }

        @Test
        @DisplayName("신고 엔티티가 없으면 CommentReportNotFoundException을 던진다")
        void 신고엔티티없음() {
            // given
            CommentReportId id = new CommentReportId(1L, 2L);
            when(commentReportRepository.existsById(id)).thenReturn(true);
            when(commentRepository.findById(2L)).thenReturn(Optional.of(newComment(2L)));
            when(commentReportRepository.findById(id)).thenReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> commentReportService.deleteReport(1L, 2L))
                    .isInstanceOf(CommentReportNotFoundException.class);
        }
    }
}
