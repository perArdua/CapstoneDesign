package com.example.campusin.infra.postsearch;

import com.example.campusin.application.postsearch.policy.BulkIndexRetryPolicy;
import com.example.campusin.domain.postsearch.PostSearch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.BulkResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostSearchIndexRepository")
class PostSearchIndexRepositoryTest {

    @Mock
    OpenSearchClient client;

    @Mock
    BulkIndexRetryPolicy bulkIndexRetryPolicy;

    PostSearchIndexRepository repository;

    @BeforeEach
    void setUp() {
        repository = new PostSearchIndexRepository(client, "posts", bulkIndexRetryPolicy);
    }

    @Test
    @DisplayName("bulkIndex는 문서를 한번에 인덱싱하고 오류가 없으면 재시도하지 않는다")
    void bulkIndex_runsOnceOnSuccess() throws Exception {
        // given
        List<PostSearch> documents = List.of(
                PostSearch.builder().id("1").title("t1").content("c1").boardId(1L).userId(2L).build()
        );
        BulkResponse response = mock(BulkResponse.class, RETURNS_DEEP_STUBS);
        when(response.errors()).thenReturn(false);
        when(client.bulk(any(BulkRequest.class))).thenReturn(response);

        // when
        assertThatCode(() -> repository.bulkIndex(documents)).doesNotThrowAnyException();

        // then
        verify(client).bulk(any(BulkRequest.class));
        verifyNoInteractions(bulkIndexRetryPolicy);
    }
}
