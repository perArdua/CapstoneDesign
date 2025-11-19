package com.example.campusin.application.postsearch.policy;

import com.example.campusin.domain.post.Post;
import com.example.campusin.infra.post.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultBatchReindexPolicy")
class DefaultBatchReindexPolicyTest {

    @Mock
    PostRepository postRepository;

    @InjectMocks
    DefaultBatchReindexPolicy defaultBatchReindexPolicy;

    @Test
    @DisplayName("마지막 ID 이후의 게시글을 최대 9000건 조회한다")
    void 다음_배치를_조회한다() {
        // given
        Long lastId = 15L;
        List<Post> expected = List.of(new Post());

        given(postRepository.findTop9000ByIdGreaterThanOrderByIdAsc(lastId)).willReturn(expected);

        // when
        List<Post> result = defaultBatchReindexPolicy.nextBatch(lastId);

        // then
        assertThat(result).isEqualTo(expected);
        verify(postRepository).findTop9000ByIdGreaterThanOrderByIdAsc(lastId);
    }
}
