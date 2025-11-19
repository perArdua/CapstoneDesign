package com.example.campusin.infra.post;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.post.PostLike;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PostLikeRepository")
class PostLikeRepositoryTest extends DataJpaTestSupport {

    @Autowired
    PostLikeRepository postLikeRepository;

    @Test
    @DisplayName("게시글 좋아요를 저장하고 조회한다")
    void saveAndFind() {
        // given
        User user = TestEntityFactory.persistUser(em, "post-like");
        Post post = TestEntityFactory.persistPost(em, "like target", user, TestEntityFactory.persistBoard(em, BoardType.Free), null);
        PostLike like = postLikeRepository.save(new PostLike(post, user));

        // when // then
        assertThat(postLikeRepository.findById(like.getId())).isPresent();
    }
}
