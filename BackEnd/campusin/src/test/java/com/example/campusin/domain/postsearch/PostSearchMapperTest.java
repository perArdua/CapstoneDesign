package com.example.campusin.domain.postsearch;

import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PostSearchMapper")
class PostSearchMapperTest {

    @Test
    @DisplayName("게시글을 검색 문서로 매핑한다")
    void 게시글을_매핑한다() {
        // given
        User user = new User();
        user.setId(10L);
        Board board = Board.builder().boardType(BoardType.Free).build();
        ReflectionTestUtils.setField(board, "id", 3L);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .user(user)
                .board(board)
                .build();
        ReflectionTestUtils.setField(post, "id", 1L);

        // when
        PostSearch postSearch = PostSearchMapper.fromPost(post);

        // then
        assertThat(postSearch.getId()).isEqualTo("1");
        assertThat(postSearch.getBoardId()).isEqualTo(3L);
        assertThat(postSearch.getUserId()).isEqualTo(10L);
        assertThat(postSearch.getTitle()).isEqualTo("제목");
    }
}
