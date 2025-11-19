package com.example.campusin.infra.comment;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.comment.dto.response.CommentsOnPostResponse;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CommentRepository")
class CommentRepositoryTest extends DataJpaTestSupport {

    @Autowired
    CommentRepository commentRepository;

    @Test
    @DisplayName("사용자와 게시글로 첫 댓글을 조회한다")
    void findFirstByUserIdAndPostId() {
        // given
        User author = TestEntityFactory.persistUser(em, "author");
        User commenter = TestEntityFactory.persistUser(em, "commenter");
        Post post = TestEntityFactory.persistPost(em, "comment target", author, TestEntityFactory.persistBoard(em, BoardType.Free), null);
        Comment comment = TestEntityFactory.persistComment(em, post, commenter, null);

        // when
        Optional<Comment> found = commentRepository.findFirstByUserIdAndPostId(commenter.getId(), post.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(comment.getId());
    }

    @Test
    @DisplayName("게시글의 부모 댓글과 대댓글을 계층 구조로 페이징 조회한다")
    void findByPost_returnsParentAndChildren() {
        // given
        User author = TestEntityFactory.persistUser(em, "parent-author");
        User childUser = TestEntityFactory.persistUser(em, "child-user");
        Post post = TestEntityFactory.persistPost(em, "post", author, TestEntityFactory.persistBoard(em, BoardType.Free), null);
        Comment parent = TestEntityFactory.persistComment(em, post, author, null);
        Comment deletedParent = TestEntityFactory.persistComment(em, post, author, null);
        deletedParent.updateDelete();
        Comment child = TestEntityFactory.persistComment(em, post, childUser, parent);
        em.flush();

        // when
        Page<CommentsOnPostResponse> page = commentRepository.findByPost(post.getId(), PageRequest.of(0, 10));

        // then
        assertThat(page.getTotalElements()).isEqualTo(1); // 삭제된 부모는 제외
        CommentsOnPostResponse first = page.getContent().get(0);
        assertThat(first.getChildrenSize()).isEqualTo(1);
        assertThat(first.getChildren().get(0).getCommentId()).isEqualTo(child.getId());
        assertThat(first.getCommentId()).isEqualTo(parent.getId());
    }
}
