package com.example.campusin.application.post;

import com.example.campusin.application.post.exception.BoardNotFoundException;
import com.example.campusin.application.post.exception.PostNotFoundException;
import com.example.campusin.application.post.exception.TagNotFoundException;
import com.example.campusin.application.user.exception.UserNotFoundException;
import com.example.campusin.application.postsearch.PostSearchIndexer;
import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.photo.Photo;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.post.PostLikeId;
import com.example.campusin.domain.post.PostReportId;
import com.example.campusin.domain.post.ReportResult;
import com.example.campusin.domain.post.ReportType;
import com.example.campusin.domain.post.dto.request.PostCreateRequest;
import com.example.campusin.domain.post.dto.request.PostUpdateRequest;
import com.example.campusin.domain.post.dto.response.PostIdResponse;
import com.example.campusin.domain.post.dto.response.PostSimpleResponse;
import com.example.campusin.domain.tag.Tag;
import com.example.campusin.domain.tag.TagType;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.board.BoardRepository;
import com.example.campusin.infra.photo.PhotoRepository;
import com.example.campusin.infra.post.PostLikeRepository;
import com.example.campusin.infra.post.PostReportRepository;
import com.example.campusin.infra.post.PostRepository;
import com.example.campusin.infra.studygroup.StudyGroupRepository;
import com.example.campusin.infra.tag.TagRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService")
class PostServiceTest {

    @Mock
    PostRepository postRepository;
    @Mock
    BoardRepository boardRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PhotoRepository photoRepository;
    @Mock
    PostLikeRepository postLikeRepository;
    @Mock
    PostReportRepository postReportRepository;
    @Mock
    StudyGroupRepository studyGroupRepository;
    @Mock
    TagRepository tagRepository;
    @Mock
    PostSearchIndexer postSearchIndexer;

    @InjectMocks
    PostService postService;

    private Board newBoard(Long id) {
        Board board = Board.builder().boardType(BoardType.Free).build();
        ReflectionTestUtils.setField(board, "id", id);
        return board;
    }

    private User newUser(Long id, String nickname) {
        User user = new User();
        user.setId(id);
        user.setNickname(nickname);
        user.setLoginId("login-" + id);
        return user;
    }

    private Tag newTag(Long id) {
        Tag tag = Tag.builder().tagType(TagType.IT).build();
        ReflectionTestUtils.setField(tag, "id", id);
        return tag;
    }

    private Post newPost(Long id, Board board, User user, Tag tag) {
        Post post = Post.builder()
                .board(board)
                .user(user)
                .title("title")
                .content("content")
                .price(1000L)
                .studyGroupId(1L)
                .tag(tag)
                .build();
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }

    @Nested
    @DisplayName("getPostsByBoard 메서드는")
    class Describe_getPostsByBoard {

        @Test
        @DisplayName("게시판이 존재하면 게시글 목록을 반환한다")
        void 게시글_목록을_반환한다() {
            // given
            Long boardId = 1L;
            Board board = newBoard(boardId);
            User user = newUser(1L, "nick");
            Tag tag = newTag(2L);
            Post post = newPost(10L, board, user, tag);

            PageRequest pageable = PageRequest.of(0, 5);
            when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
            when(postRepository.findPostsByBoardId(boardId, pageable))
                    .thenReturn(new PageImpl<>(List.of(post), pageable, 1));

            // when
            Page<PostSimpleResponse> responses = postService.getPostsByBoard(boardId, pageable);

            // then
            assertThat(responses.getTotalElements()).isEqualTo(1);
            PostSimpleResponse response = responses.getContent().get(0);
            assertThat(response.getPostId()).isEqualTo(10L);
            assertThat(response.getBoardSimpleResponse().getBoardType()).isEqualTo(BoardType.Free);
            assertThat(response.getNickname()).isEqualTo("nick");
        }

        @Test
        @DisplayName("게시판이 없으면 BoardNotFoundException을 던진다")
        void 게시판없음() {
            // given
            when(boardRepository.findById(1L)).thenReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> postService.getPostsByBoard(1L, PageRequest.of(0, 1)))
                    .isInstanceOf(BoardNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getPostsByTag 메서드는")
    class Describe_getPostsByTag {

        @Test
        @DisplayName("게시판과 태그가 존재하면 게시글 목록을 반환한다")
        void 태그로_조회한다() {
            // given
            Long boardId = 1L;
            Long tagId = 2L;
            Board board = newBoard(boardId);
            Tag tag = newTag(tagId);
            User user = newUser(1L, "nick");
            Post post = newPost(5L, board, user, tag);
            PageRequest pageable = PageRequest.of(0, 3);

            when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
            when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
            when(postRepository.findPostsByTagId(boardId, tagId, pageable))
                    .thenReturn(new PageImpl<>(List.of(post), pageable, 1));

            // when
            Page<PostSimpleResponse> responses = postService.getPostsByTag(boardId, tagId, pageable);

            // then
            assertThat(responses.getTotalElements()).isEqualTo(1);
            verify(postRepository).findPostsByTagId(boardId, tagId, pageable);
        }

        @Test
        @DisplayName("태그가 없으면 TagNotFoundException을 던진다")
        void 태그없음() {
            // given
            when(boardRepository.findById(1L)).thenReturn(Optional.of(newBoard(1L)));
            when(tagRepository.findById(2L)).thenReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> postService.getPostsByTag(1L, 2L, PageRequest.of(0, 1)))
                    .isInstanceOf(TagNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("createPost 메서드는")
    class Describe_createPost {

        @Test
        @DisplayName("게시판/사용자/태그 조회 후 게시글과 사진을 저장하고 인덱싱한다")
        void 게시글을_저장한다() {
            // given
            Long boardId = 1L;
            Long tagId = 2L;
            Long userId = 3L;
            Board board = newBoard(boardId);
            Tag tag = newTag(tagId);
            User user = newUser(userId, "nick");

            PostCreateRequest request = PostCreateRequest.builder()
                    .title("제목")
                    .content("내용")
                    .photos(List.of("p1", "p2"))
                    .price(100L)
                    .studyGroupId(5L)
                    .build();

            ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
            when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
            when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
                Post saved = invocation.getArgument(0);
                if (saved.getId() == null) {
                    ReflectionTestUtils.setField(saved, "id", 9L);
                }
                return saved;
            });

            // when
            PostIdResponse response = postService.createPost(boardId, tagId, userId, request);

            // then
            assertThat(response.getPostId()).isEqualTo(9L);
            verify(postRepository, times(2)).save(postCaptor.capture());
            List<Post> savedCalls = postCaptor.getAllValues();
            Post saved = savedCalls.get(0);
            assertThat(saved.getBoard()).isEqualTo(board);
            assertThat(saved.getUser()).isEqualTo(user);
            assertThat(saved.getTag()).isEqualTo(tag);
            assertThat(saved.getTitle()).isEqualTo("제목");
            assertThat(saved.getContent()).isEqualTo("내용");
            verify(photoRepository, times(2)).save(any(Photo.class));
            verify(postSearchIndexer).index(saved);
        }

        @Test
        @DisplayName("게시판이 없으면 BoardNotFoundException을 던진다")
        void 게시판없음() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> postService.createPost(1L, 2L, 3L, new PostCreateRequest()))
                    .isInstanceOf(BoardNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updatePost 메서드는")
    class Describe_updatePost {

        @Test
        @DisplayName("게시글을 수정하고 ID를 반환한다")
        void 수정한다() {
            // given
            Post post = newPost(10L, newBoard(1L), newUser(2L, "nick"), newTag(3L));
            PostUpdateRequest request = PostUpdateRequest.builder()
                    .title("new")
                    .content("new-content")
                    .price(200L)
                    .studyGroupId(8L)
                    .photos(List.of(new Photo("c1")))
                    .build();

            when(postRepository.findById(10L)).thenReturn(Optional.of(post));
            when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            PostIdResponse response = postService.updatePost(10L, request);

            // then
            assertThat(response.getPostId()).isEqualTo(10L);
            assertThat(post.getTitle()).isEqualTo("new");
            assertThat(post.getContent()).isEqualTo("new-content");
            assertThat(post.getPrice()).isEqualTo(200L);
            assertThat(post.getStudyGroupId()).isEqualTo(8L);
        }

        @Test
        @DisplayName("게시글이 없으면 PostNotFoundException을 던진다")
        void 게시글없음() {
            // given
            when(postRepository.findById(1L)).thenReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> postService.updatePost(1L, new PostUpdateRequest()))
                    .isInstanceOf(PostNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("likePost/unlikePost 메서드는")
    class Describe_likePost {

        @Test
        @DisplayName("이미 좋아요가 있으면 false를 반환하고 저장하지 않는다")
        void 이미_좋아요() {
            when(postLikeRepository.existsById(new PostLikeId(1L, 2L))).thenReturn(true);

            boolean result = postService.likePost(1L, 2L);

            assertThat(result).isFalse();
            verify(postRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("좋아요가 없으면 저장하고 true를 반환한다")
        void 좋아요_저장() {
            Post post = newPost(2L, newBoard(1L), newUser(1L, "nick"), newTag(3L));
            when(postLikeRepository.existsById(new PostLikeId(1L, 2L))).thenReturn(false);
            when(postRepository.findById(2L)).thenReturn(Optional.of(post));
            when(userRepository.findById(1L)).thenReturn(Optional.of(newUser(1L, "nick")));

            boolean result = postService.likePost(1L, 2L);

            assertThat(result).isTrue();
            verify(postLikeRepository).save(any());
        }

        @Test
        @DisplayName("unlike는 좋아요 없으면 false, 있으면 삭제하고 true를 반환한다")
        void 좋아요_취소() {
            Post post = newPost(2L, newBoard(1L), newUser(1L, "nick"), newTag(3L));
            when(postLikeRepository.existsById(new PostLikeId(1L, 2L))).thenReturn(false);

            assertThat(postService.unlikePost(1L, 2L)).isFalse();

            when(postLikeRepository.existsById(new PostLikeId(1L, 2L))).thenReturn(true);
            when(postRepository.findById(2L)).thenReturn(Optional.of(post));
            when(userRepository.findById(1L)).thenReturn(Optional.of(newUser(1L, "nick")));

            assertThat(postService.unlikePost(1L, 2L)).isTrue();
            verify(postLikeRepository).deleteById(new PostLikeId(1L, 2L));
        }
    }

    @Nested
    @DisplayName("reportPost 메서드는")
    class Describe_reportPost {

        @Test
        @DisplayName("이미 신고한 경우 ALREADY_REPORTED를 반환한다")
        void 이미_신고() {
            when(postReportRepository.existsById(new PostReportId(1L, 2L))).thenReturn(true);

            ReportResult result = postService.reportPost(1L, 2L, ReportType.ABUSE);

            assertThat(result).isEqualTo(ReportResult.ALREADY_REPORTED);
        }

        @Test
        @DisplayName("신고 점수가 임계 이상이면 POST_HIDDEN을 반환하고 숨김 처리한다")
        void 숨김처리() {
            Post post = newPost(2L, newBoard(1L), newUser(3L, "nick"), newTag(4L));
            when(postReportRepository.existsById(new PostReportId(1L, 2L))).thenReturn(false);
            when(postRepository.findById(2L)).thenReturn(Optional.of(post));
            when(userRepository.findById(1L)).thenReturn(Optional.of(newUser(1L, "u")));
            when(postReportRepository.sumReportScore(2L)).thenReturn(PostService.REPORT_HIDE_THRESHOLD);

            ReportResult result = postService.reportPost(1L, 2L, ReportType.ABUSE);

            assertThat(result).isEqualTo(ReportResult.POST_HIDDEN);
            assertThat(post.isHidden()).isTrue();
        }
    }

    @Nested
    @DisplayName("blockPost/unblockPost 메서드는")
    class Describe_block_unblock {

        @Test
        @DisplayName("blockPost는 제목/내용을 신고 처리 문구로 바꾼다")
        void 블록한다() {
            // given
            Post post = newPost(1L, newBoard(1L), newUser(1L, "nick"), newTag(1L));
            when(postRepository.findById(1L)).thenReturn(Optional.of(post));

            // when
            postService.blockPost(1L);

            // then
            assertThat(post.getTitle()).isEqualTo("신고 완료 처리 된 게시글입니다.");
            assertThat(post.getContent()).isEqualTo("신고 완료 처리 된 게시글입니다.");
        }

        @Test
        @DisplayName("unblockPost는 신고 횟수를 0으로 만들고 report 기록을 지운다")
        void 언블록한다() {
            // given
            Post post = newPost(1L, newBoard(1L), newUser(1L, "nick"), newTag(1L));
            post.increaseReportCount();
            when(postRepository.findById(1L)).thenReturn(Optional.of(post));

            // when
            postService.unblockPost(1L);

            // then
            assertThat(post.getReportCount()).isEqualTo(0);
            verify(postReportRepository).deleteByPostId(1L);
        }
    }
}
