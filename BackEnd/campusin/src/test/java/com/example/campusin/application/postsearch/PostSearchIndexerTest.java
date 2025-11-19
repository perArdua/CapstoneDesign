package com.example.campusin.application.postsearch;

import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.postsearch.PostSearch;
import com.example.campusin.infra.postsearch.PostSearchRepository;
import com.example.campusin.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostSearchIndexer")
class PostSearchIndexerTest {

    @Mock
    PostSearchRepository postSearchRepository;

    @InjectMocks
    PostSearchIndexer postSearchIndexer;

    @Nested
    @DisplayName("index 메서드는")
    class Describe_index {

        @Test
        @DisplayName("단일 게시글을 PostSearch로 변환해 bulkIndex로 저장한다")
        void 단일_게시글을_색인한다() {
            // given
            User user = new User();
            user.setId(5L);
            Board board = Board.builder().boardType(BoardType.Free).build();
            ReflectionTestUtils.setField(board, "id", 7L);

            Post post = Post.builder()
                    .title("title")
                    .content("content")
                    .user(user)
                    .board(board)
                    .price(1000L)
                    .studyGroupId(1L)
                    .build();
            ReflectionTestUtils.setField(post, "id", 11L);

            ArgumentCaptor<List<PostSearch>> captor = ArgumentCaptor.forClass(List.class);

            // when
            postSearchIndexer.index(post);

            // then
            verify(postSearchRepository).bulkIndex(captor.capture());
            List<PostSearch> documents = captor.getValue();
            assertThat(documents).singleElement().satisfies(doc -> {
                assertThat(doc.getId()).isEqualTo("11");
                assertThat(doc.getTitle()).isEqualTo("title");
                assertThat(doc.getContent()).isEqualTo("content");
                assertThat(doc.getBoardId()).isEqualTo(7L);
                assertThat(doc.getUserId()).isEqualTo(5L);
            });
        }
    }

    @Nested
    @DisplayName("bulkIndex 메서드는")
    class Describe_bulkIndex {

        @Test
        @DisplayName("여러 게시글을 PostSearch 리스트로 변환해 bulkIndex 호출에 전달한다")
        void 여러_게시글을_색인한다() {
            // given
            User user = new User();
            user.setId(9L);
            Board board = Board.builder().boardType(BoardType.Question).build();
            ReflectionTestUtils.setField(board, "id", 3L);

            Post first = Post.builder().title("t1").content("c1").user(user).board(board).build();
            Post second = Post.builder().title("t2").content("c2").user(user).board(board).build();
            ReflectionTestUtils.setField(first, "id", 1L);
            ReflectionTestUtils.setField(second, "id", 2L);

            ArgumentCaptor<List<PostSearch>> captor = ArgumentCaptor.forClass(List.class);

            // when
            postSearchIndexer.bulkIndex(List.of(first, second));

            // then
            verify(postSearchRepository).bulkIndex(captor.capture());
            List<PostSearch> documents = captor.getValue();
            assertThat(documents).hasSize(2);
            assertThat(documents.get(0).getId()).isEqualTo("1");
            assertThat(documents.get(1).getId()).isEqualTo("2");
        }
    }
}
