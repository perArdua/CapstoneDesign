package com.example.campusin.application.comment;

import com.example.campusin.application.comment.exception.CommentNotFoundException;
import com.example.campusin.application.post.exception.PostNotFoundException;
import com.example.campusin.application.user.exception.UserNotFoundException;
import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.comment.dto.request.CommentCreateRequest;
import com.example.campusin.domain.comment.dto.response.CommentCreateResponse;
import com.example.campusin.domain.comment.dto.response.CommentsOnPostResponse;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.board.BoardRepository;
import com.example.campusin.infra.comment.CommentReportRepository;
import com.example.campusin.infra.comment.CommentRepository;
import com.example.campusin.infra.post.PostRepository;
import com.example.campusin.infra.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService")
class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    BoardRepository boardRepository;
    @Mock
    CommentReportRepository commentReportRepository;

    @InjectMocks
    CommentService commentService;

    private User newUser(Long id, String nickname) {
        User user = new User();
        user.setId(id);
        user.setNickname(nickname);
        user.setLoginId("login-" + id);
        return user;
    }

    private Post newPost(Long id, BoardType boardType, User owner) {
        Board board = Board.builder().boardType(boardType).build();
        Post post = Post.builder()
                .board(board)
                .user(owner)
                .title("title")
                .content("content")
                .price(0L)
                .studyGroupId(-1L)
                .build();
        ReflectionTestUtils.setField(board, "id", 1L);
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }

    private Comment newComment(Long id, Post post, User user, String content) {
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(content)
                .parent(null)
                .isAnswer(false)
                .isAdopted(false)
                .build();
        ReflectionTestUtils.setField(comment, "id", id);
        return comment;
    }

    @Nested
    @DisplayName("createComment 메서드는")
    class Describe_createComment {

        @Test
        @DisplayName("게시판이 Question이면 isAnswer=true로 저장하고 응답을 반환한다")
        void 질문게시판이면_답변표시() {
            // given
            User user = newUser(1L, "nick");
            Post post = newPost(2L, BoardType.Question, newUser(3L, "owner"));
            CommentCreateRequest request = new CommentCreateRequest();
            ReflectionTestUtils.setField(request, "content", "내용");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(postRepository.findById(2L)).thenReturn(Optional.of(post));
            when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            CommentCreateResponse response = commentService.createComment(1L, request, 2L);

            // then
            assertThat(response.getContent()).isEqualTo("내용");
            verify(commentRepository).save(any(Comment.class));
            // isAnswer should be set when board is Question
            assertThat(post.getCommentList()).first().satisfies(c -> assertThat(c.getIsAnswer()).isTrue());
        }

        @Test
        @DisplayName("부모 ID가 있으면 존재 여부를 확인한다")
        void 부모댓글을_조회한다() {
            // given
            User user = newUser(1L, "nick");
            Post post = newPost(2L, BoardType.Free, newUser(3L, "owner"));
            Comment parent = newComment(5L, post, user, "parent");
            CommentCreateRequest request = new CommentCreateRequest();
            ReflectionTestUtils.setField(request, "content", "child");
            ReflectionTestUtils.setField(request, "parentId", 5L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(postRepository.findById(2L)).thenReturn(Optional.of(post));
            when(commentRepository.findById(5L)).thenReturn(Optional.of(parent));
            when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            CommentCreateResponse response = commentService.createComment(1L, request, 2L);

            // then
            verify(commentRepository).findById(5L);
        }

        @Test
        @DisplayName("사용자 없으면 UserNotFoundException을 던진다")
        void 사용자없음() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            CommentCreateRequest request = new CommentCreateRequest();
            ReflectionTestUtils.setField(request, "content", "c");
            assertThatThrownBy(() -> commentService.createComment(1L, request, 2L))
                    .isInstanceOf(UserNotFoundException.class);
            verifyNoInteractions(commentRepository);
        }
    }

    @Nested
    @DisplayName("searchCommentByPost 메서드는")
    class Describe_searchCommentByPost {

        @Test
        @DisplayName("게시글이 없으면 PostNotFoundException을 던진다")
        void 게시글없음() {
            // given
            when(postRepository.existsById(1L)).thenReturn(false);

            // when // then
            assertThatThrownBy(() -> commentService.searchCommentByPost(1L, PageRequest.of(0, 1)))
                    .isInstanceOf(PostNotFoundException.class);
        }

        @Test
        @DisplayName("댓글을 페이지로 반환한다")
        void 댓글목록() {
            // given
            when(postRepository.existsById(1L)).thenReturn(true);
            PageRequest pageable = PageRequest.of(0, 2);
            CommentsOnPostResponse projection = new CommentsOnPostResponse(
                    1L, 1L, 1L, 0, 0, "nick", "c", false, 1L, null
            );
            when(commentRepository.findByPost(1L, pageable))
                    .thenReturn(new PageImpl<>(List.of(projection), pageable, 1));

            // when
            Page<CommentsOnPostResponse> responses = commentService.searchCommentByPost(1L, pageable);

            // then
            assertThat(responses.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("deleteComment 메서드는")
    class Describe_deleteComment {

        @Test
        @DisplayName("댓글을 삭제 플래그로 표시한다")
        void 삭제한다() {
            // given
            User user = newUser(1L, "u");
            Comment comment = newComment(2L, newPost(3L, BoardType.Free, user), user, "c");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));

            // when
            commentService.deleteComment(1L, 2L);

            // then
            assertThat(comment.getIsDelete()).isTrue();
        }
    }

    @Nested
    @DisplayName("updateIsAdopted 메서드는")
    class Describe_updateIsAdopted {

        @Test
        @DisplayName("게시글 작성자가 호출하면 isAdopted를 true로 만든다")
        void 채택한다() {
            // given
            User owner = newUser(1L, "owner");
            Post post = newPost(3L, BoardType.Question, owner);
            Comment comment = newComment(2L, post, newUser(4L, "c"), "c");
            when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));

            // when
            commentService.updateIsAdopted(2L, 1L);

            // then
            assertThat(comment.getIsAdopted()).isTrue();
        }

        @Test
        @DisplayName("작성자가 아니면 UserNotFoundException을 던진다")
        void 작성자아님() {
            // given
            User owner = newUser(1L, "owner");
            Post post = newPost(3L, BoardType.Question, owner);
            Comment comment = newComment(2L, post, newUser(4L, "c"), "c");
            when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));

            // when // then
            assertThatThrownBy(() -> commentService.updateIsAdopted(2L, 99L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("blockComment/unblockComment 메서드는")
    class Describe_block_unblock {

        @Test
        @DisplayName("block은 내용 변환 후 저장한다")
        void 블록한다() {
            // given
            Comment comment = newComment(1L, newPost(2L, BoardType.Free, newUser(3L, "o")), newUser(4L, "u"), "orig");
            when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

            // when
            commentService.blockComment(1L);

            // then
            assertThat(comment.getContent()).isEqualTo("신고 완료 처리 된 댓글입니다.");
            verify(commentRepository).save(comment);
        }

        @Test
        @DisplayName("unblock은 신고들을 삭제하고 보고 컬렉션을 null로 만든다")
        void 언블록한다() {
            // given
            Comment comment = newComment(1L, newPost(2L, BoardType.Free, newUser(3L, "o")), newUser(4L, "u"), "orig");
            when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

            // when
            commentService.unblockComment(1L);

            // then
            assertThat(comment.getReports()).isNull();
            verify(commentReportRepository).deleteByCommentId(1L);
            verify(commentRepository).save(comment);
        }
    }
}
