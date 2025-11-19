package com.example.campusin.application.postsearch;

import com.example.campusin.application.postsearch.policy.BatchReindexPolicy;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.post.PostRepository;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostReindexer")
class PostReindexerTest {

    @Mock
    PostRepository postRepository;
    @Mock
    PostSearchIndexer postSearchIndexer;
    @Mock
    BatchReindexPolicy batchReindexPolicy;

    @InjectMocks
    PostReindexer postReindexer;

    @Nested
    @DisplayName("reindexAll 메서드는")
    class Describe_reindexAll {

        @Test
        @DisplayName("배치를 순회하며 검색 인덱서를 호출한다")
        void 배치를_순회한다() {
            // given
            Post first = Post.builder().title("t1").content("c1").user(new User()).build();
            Post second = Post.builder().title("t2").content("c2").user(new User()).build();
            ReflectionTestUtils.setField(first, "id", 1L);
            ReflectionTestUtils.setField(second, "id", 2L);

            given(batchReindexPolicy.nextBatch(0L)).willReturn(List.of(first, second));
            given(batchReindexPolicy.nextBatch(2L)).willReturn(List.of());

            ArgumentCaptor<List<Post>> batchCaptor = ArgumentCaptor.forClass(List.class);

            // when
            postReindexer.reindexAll();

            // then
            verify(batchReindexPolicy).nextBatch(0L);
            verify(batchReindexPolicy).nextBatch(2L);
            verify(postSearchIndexer).bulkIndex(batchCaptor.capture());
            List<Post> indexed = batchCaptor.getValue();
            assertThat(indexed).containsExactly(first, second);
            verifyNoInteractions(postRepository);
        }
    }
}
