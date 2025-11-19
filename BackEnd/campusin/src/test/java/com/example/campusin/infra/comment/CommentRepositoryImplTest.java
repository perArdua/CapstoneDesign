package com.example.campusin.infra.comment;

import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.comment.dto.response.CommentsOnPostResponse;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.example.campusin.domain.comment.QComment.comment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentRepositoryImpl")
class CommentRepositoryImplTest {

    @Mock
    EntityManager entityManager;

    @InjectMocks
    CommentRepositoryImpl commentRepository;

    @Test
    @DisplayName("게시글의 부모/자식 댓글을 페이지로 조회한다")
    void 대댓글_포함_조회한다() {
        // given
        JPAQueryFactory queryFactory = mock(JPAQueryFactory.class);
        ReflectionTestUtils.setField(commentRepository, "queryFactory", queryFactory);

        JPAQuery<Comment> parentQuery = mock(JPAQuery.class, RETURNS_SELF);
        JPAQuery<Comment> childQuery = mock(JPAQuery.class, RETURNS_SELF);
        JPAQuery<Long> countQuery = mock(JPAQuery.class, RETURNS_SELF);

        given(queryFactory.select(comment)).willReturn(parentQuery, childQuery);
        given(queryFactory.select(Wildcard.count)).willReturn(countQuery);

        PageRequest pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setId(1L);
        user.setNickname("tester");
        Board board = Board.builder().boardType(BoardType.Free).build();
        ReflectionTestUtils.setField(board, "id", 3L);
        Post post = Post.builder().title("t").content("c").user(user).board(board).build();
        ReflectionTestUtils.setField(post, "id", 4L);

        Comment parentKeep = Comment.builder().user(user).post(post).content("parent").isAnswer(false).isAdopted(false).build();
        ReflectionTestUtils.setField(parentKeep, "id", 10L);
        Comment child = Comment.builder().parent(parentKeep).user(user).post(post).content("child").isAnswer(false).isAdopted(false).build();
        ReflectionTestUtils.setField(child, "id", 11L);

        Comment deletedParent = Comment.builder().user(user).post(post).content("deleted").isAnswer(false).isAdopted(false).build();
        ReflectionTestUtils.setField(deletedParent, "id", 12L);
        deletedParent.updateDelete();

        given(parentQuery.fetch()).willReturn(List.of(parentKeep, deletedParent));
        given(childQuery.fetch()).willReturn(List.of(child));
        given(countQuery.fetch()).willReturn(List.of(2L));

        // when
        // when
        Page<CommentsOnPostResponse> page = commentRepository.findByPost(post.getId(), pageable);

        // then
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).hasSize(1);
        CommentsOnPostResponse response = page.getContent().get(0);
        assertThat(response.getCommentId()).isEqualTo(10L);
        assertThat(response.getChildrenSize()).isEqualTo(1);
        assertThat(response.getChildren().get(0).getCommentId()).isEqualTo(11L);
    }
}
