package com.example.campusin.domain.post;

import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.photo.Photo;
import com.example.campusin.domain.post.dto.request.PostUpdateRequest;
import com.example.campusin.domain.tag.Tag;
import com.example.campusin.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Post 도메인")
class PostTest {

    @Nested
    @DisplayName("updatePost 메서드는")
    class Describe_updatePost {

        @Test
        @DisplayName("제목/내용/가격/사진/스터디그룹을 업데이트한다")
        void 필드를_업데이트한다() {
            // given
            Post post = Post.builder()
                    .title("old")
                    .content("old content")
                    .user(new User())
                    .board(Board.builder().boardType(BoardType.Free).build())
                    .price(100L)
                    .studyGroupId(1L)
                    .tag(new Tag())
                    .build();

            Photo oldPhoto = new Photo("old");
            post.setPhotos(List.of(oldPhoto));
            Photo newPhoto = new Photo("new");

            PostUpdateRequest request = PostUpdateRequest.builder()
                    .title("new title")
                    .content("new content")
                    .photos(List.of(newPhoto))
                    .price(200L)
                    .studyGroupId(2L)
                    .build();

            // when
            post.updatePost(request);

            // then
            assertThat(post.getTitle()).isEqualTo("new title");
            assertThat(post.getContent()).isEqualTo("new content");
            assertThat(post.getPrice()).isEqualTo(200L);
            assertThat(post.getStudyGroupId()).isEqualTo(2L);
            assertThat(post.getPhotos()).containsExactly(newPhoto);
            assertThat(newPhoto.getPost()).isEqualTo(post);
            assertThat(oldPhoto.getPost()).isNull();
        }
    }

    @Nested
    @DisplayName("카운트 조정 메서드는")
    class Describe_counts {

        @Test
        @DisplayName("댓글/좋아요/신고 카운트를 증감한다")
        void 카운트를_증감한다() {
            // given
            Post post = Post.builder()
                    .title("t")
                    .content("c")
                    .user(new User())
                    .board(Board.builder().boardType(BoardType.Free).build())
                    .build();

            // when
            post.increaseCommentCount();
            post.increaseLikeCount();
            post.increaseLikeCount();
            post.decreaseLikeCount();
            post.increaseReportCount();
            post.decreaseReportCount();

            // then
            assertThat(post.getCommentCount()).isEqualTo(1);
            assertThat(post.getLikeCount()).isEqualTo(1);
            assertThat(post.getReportCount()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("가시성 메서드는")
    class Describe_visibility {

        @Test
        @DisplayName("숨김/숨김 해제 플래그를 토글한다")
        void 숨김을_토글한다() {
            // given
            Post post = Post.builder()
                    .title("t")
                    .content("c")
                    .user(new User())
                    .board(Board.builder().boardType(BoardType.Free).build())
                    .build();

            // when
            post.hide();
            post.unhide();

            // then
            assertThat(post.isHidden()).isFalse();
        }
    }
}
