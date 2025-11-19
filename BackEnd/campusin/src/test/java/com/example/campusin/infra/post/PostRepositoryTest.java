package com.example.campusin.infra.post;

import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.tag.Tag;
import com.example.campusin.domain.tag.TagType;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PostRepository")
public class PostRepositoryTest extends DataJpaTestSupport {

    @Autowired
    PostRepository postRepository;

    @Nested
    @DisplayName("findPostsByBoardId 메서드는")
    class Describe_findPostsByBoardId {

        @Test
        @DisplayName("특정 게시판의 글만 페이지로 반환한다")
        void 특정_게시판만_조회() {
            // given
            User author = persistUser("author1");
            Board free = persistBoard(BoardType.Free);
            Board question = persistBoard(BoardType.Question);

            Post target = persistPost("free", author, free, null);
            persistPost("question", author, question, null);

            // when
            Page<Post> result = postRepository.findPostsByBoardId(free.getId(), PageRequest.of(0, 10));

            // then
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(target.getId());
        }
    }

    @Nested
    @DisplayName("findPostsByUserId 메서드는")
    class Describe_findPostsByUserId {

        @Test
        @DisplayName("해당 사용자가 작성한 글만 반환한다")
        void 특정_사용자_게시글만_반환() {
            // given
            User author = persistUser("author2");
            User other = persistUser("other");
            Board board = persistBoard(BoardType.Free);

            Post mine = persistPost("mine", author, board, null);
            persistPost("others", other, board, null);

            // when
            Page<Post> result = postRepository.findPostsByUserId(author.getId(), PageRequest.of(0, 10));

            // then
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(mine.getId());
        }
    }

    @Nested
    @DisplayName("findPostIdsThatUserCommentedAt 메서드는")
    class Describe_findPostIdsThatUserCommentedAt {

        @Test
        @DisplayName("사용자가 댓글을 남긴 게시글 ID 목록을 반환한다")
        void 댓글_남긴_글_ID만_반환() {
            // given
            User author = persistUser("author3");
            User commenter = persistUser("commenter");
            Board board = persistBoard(BoardType.Free);

            Post post1 = persistPost("p1", author, board, null);
            Post post2 = persistPost("p2", author, board, null);
            persistComment(post1, commenter);
            persistComment(post2, commenter);

            // when
            List<Long> ids = postRepository.findPostIdsThatUserCommentedAt(commenter.getId());

            // then
            assertThat(ids).containsExactlyInAnyOrder(post1.getId(), post2.getId());
        }
    }

    @Nested
    @DisplayName("findPostsByTagId 메서드는")
    class Describe_findPostsByTagId {

        @Test
        @DisplayName("해당 게시판+태그에 속한 글만 반환한다")
        void 태그와_게시판으로_조회() {
            // given
            User author = persistUser("author4");
            Board board = persistBoard(BoardType.Free);
            Tag tag = persistTag(TagType.IT);

            Post matched = persistPost("match", author, board, tag);
            persistPost("other-board", author, persistBoard(BoardType.Question), tag);
            persistPost("other-tag", author, board, persistTag(TagType.Math));

            // when
            Page<Post> result = postRepository.findPostsByTagId(board.getId(), tag.getId(), PageRequest.of(0, 10));

            // then
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(matched.getId());
        }
    }

    @Nested
    @DisplayName("findPostsThatUserCommentedAt 메서드는")
    class Describe_findPostsThatUserCommentedAt {

        @Test
        @DisplayName("댓글 단 게시글 ID 목록을 받아 페이지로 반환한다")
        void 댓글단_게시글을_페이지로_반환() {
            // given
            User author = persistUser("author5");
            Board board = persistBoard(BoardType.Free);
            Post post1 = persistPost("p1", author, board, null);
            Post post2 = persistPost("p2", author, board, null);
            List<Long> ids = List.of(post1.getId(), post2.getId());

            // when
            Page<Post> result = postRepository.findPostsThatUserCommentedAt(ids, PageRequest.of(0, 10));

            // then
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent()).extracting(Post::getId)
                    .containsExactlyInAnyOrder(post1.getId(), post2.getId());
        }
    }

    @Nested
    @DisplayName("findPostById 메서드는")
    class Describe_findPostById {

        @Test
        @DisplayName("게시글을 ID로 조회한다")
        void 게시글을_ID로_조회() {
            // given
            User author = persistUser("author6");
            Post post = persistPost("title", author, persistBoard(BoardType.Question), null);

            // when
            Post found = postRepository.findPostById(post.getId()).orElseThrow();

            // then
            assertThat(found.getId()).isEqualTo(post.getId());
            assertThat(found.getUser().getId()).isEqualTo(author.getId());
        }
    }

    @Nested
    @DisplayName("searchPosts 메서드는")
    class Describe_searchPosts {

        @Test
        @DisplayName("제목/내용에 키워드를 포함한 글을 반환한다")
        void 제목과_내용으로_검색() {
            // given
            User author = persistUser("author7");
            Board board = persistBoard(BoardType.Free);
            Post matchedTitle = persistPost("Hello World", author, board, null);
            persistPost("Other", author, board, null);
            persistPost("has keyword inside content", author, board, null);

            // when
            Page<Post> result = postRepository.searchPosts("hello world", PageRequest.of(0, 10));

            // then
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(matchedTitle.getId());
        }
    }

    @Nested
    @DisplayName("searchPostsAtBoard 메서드는")
    class Describe_searchPostsAtBoard {

        @Test
        @DisplayName("특정 게시판 내에서만 키워드로 검색한다")
        void 게시판과_키워드로_검색() {
            // given
            User author = persistUser("author8");
            Board free = persistBoard(BoardType.Free);
            Board question = persistBoard(BoardType.Question);
            Post target = persistPost("target text", author, free, null);
            persistPost("target text", author, question, null);
            persistPost("other", author, free, null);

            // when
            Page<Post> result = postRepository.searchPostsAtBoard("targettext", free.getId(), PageRequest.of(0, 10));

            // then
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(target.getId());
        }
    }

    @Nested
    @DisplayName("findCommentsByPost 메서드는")
    class Describe_findCommentsByPost {

        @Test
        @DisplayName("게시글에 달린 댓글을 페이지로 반환한다")
        void 게시글_댓글을_페이지로_반환() {
            // given
            User author = persistUser("author9");
            User commenter = persistUser("comment-user");
            Board board = persistBoard(BoardType.Free);
            Post post = persistPost("post-with-comments", author, board, null);
            persistComment(post, commenter);
            persistComment(post, commenter);

            // when
            Page<Comment> result = postRepository.findCommentsByPost(post.getId(), PageRequest.of(0, 10));

            // then
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent()).allMatch(comment -> comment.getPost().getId().equals(post.getId()));
        }
    }

    @Nested
    @DisplayName("findPostsByStudyGroupId 메서드는")
    class Describe_findPostsByStudyGroupId {

        @Test
        @DisplayName("특정 스터디 그룹 ID의 글만 반환한다")
        void 스터디그룹_ID로_조회() {
            // given
            User author = persistUser("author10");
            Board board = persistBoard(BoardType.Free);
            Post matched1 = persistPost("study1", author, board, null);
            matched1.setStudyGroupId(100L);
            Post matched2 = persistPost("study2", author, board, null);
            matched2.setStudyGroupId(100L);
            persistPost("other", author, board, null);
            em.flush();

            // when
            Page<Post> result = postRepository.findPostsByStudyGroupId(100L, PageRequest.of(0, 10));

            // then
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent()).extracting(Post::getId)
                    .containsExactlyInAnyOrder(matched1.getId(), matched2.getId());
        }
    }

    @Nested
    @DisplayName("findTop9000ByIdGreaterThanOrderByIdAsc 메서드는")
    class Describe_findTop9000ByIdGreaterThanOrderByIdAsc {

        @Test
        @DisplayName("주어진 ID보다 큰 게시글을 ID 오름차순으로 반환한다")
        void 기준_ID_초과분만_오름차순_반환() {
            // given
            User author = persistUser("author11");
            Board board = persistBoard(BoardType.Free);
            Post first = persistPost("first", author, board, null);
            Post second = persistPost("second", author, board, null);
            Post third = persistPost("third", author, board, null);
            em.flush();

            // when
            List<Post> result = postRepository.findTop9000ByIdGreaterThanOrderByIdAsc(first.getId());

            // then
            assertThat(result).extracting(Post::getId)
                    .containsExactly(second.getId(), third.getId());
        }
    }

    private User persistUser(String loginId) {
        return TestEntityFactory.persistUser(em, loginId);
    }

    private Board persistBoard(BoardType type) {
        return TestEntityFactory.persistBoard(em, type);
    }

    private Tag persistTag(TagType type) {
        return TestEntityFactory.persistTag(em, type);
    }

    private Post persistPost(String title, User author, Board board, Tag tag) {
        return TestEntityFactory.persistPost(em, title, author, board, tag);
    }

    private void persistComment(Post post, User commenter) {
        TestEntityFactory.persistComment(em, post, commenter, null);
    }
}
