package com.example.campusin.infra.comment;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.comment.CommentLike;
import com.example.campusin.domain.comment.CommentLikeId;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CommentLikeRepository")
class CommentLikeRepositoryTest extends DataJpaTestSupport {

    @Autowired
    CommentLikeRepository commentLikeRepository;

    @Test
    @DisplayName("댓글 좋아요를 저장하고 ID로 조회한다")
    void saveAndFind() {
        // given
        User author = TestEntityFactory.persistUser(em, "comment-like-author");
        User liker = TestEntityFactory.persistUser(em, "liker");
        Post post = TestEntityFactory.persistPost(em, "post", author, TestEntityFactory.persistBoard(em, BoardType.Free), null);
        Comment comment = TestEntityFactory.persistComment(em, post, liker, null);

        CommentLike like = commentLikeRepository.save(new CommentLike(liker, comment));

        // when // then
        assertThat(commentLikeRepository.findById(new CommentLikeId(liker.getId(), comment.getId()))).isPresent();
        assertThat(like.getId()).isEqualTo(new CommentLikeId(liker.getId(), comment.getId()));
    }
}
