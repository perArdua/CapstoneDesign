package com.example.campusin.infra.photo;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.photo.Photo;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PhotoRepository")
class PhotoRepositoryTest extends DataJpaTestSupport {

    @Autowired
    PhotoRepository photoRepository;

    @Test
    @DisplayName("게시글 사진을 저장하고 조회한다")
    void saveAndFind() {
        // given
        User author = TestEntityFactory.persistUser(em, "photo-author");
        Post post = TestEntityFactory.persistPost(em, "with photo", author, TestEntityFactory.persistBoard(em, BoardType.Free), null);
        Photo photo = new Photo("binary");
        photo.setPost(post);
        Photo saved = photoRepository.save(photo);

        // when // then
        assertThat(photoRepository.findById(saved.getId())).isPresent();
    }
}
