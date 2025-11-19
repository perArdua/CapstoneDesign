package com.example.campusin.infra.postsearch;

import com.example.campusin.domain.postsearch.PostSearch;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.action.ActionListener;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostSearchRepository")
class PostSearchRepositoryTest {

    @Mock
    ObjectMapper objectMapper;

    @Mock
    RestHighLevelClient restHighLevelClient;

    @Mock
    PostSearchIndexRepository indexRepository;

    @Test
    @DisplayName("키셋 검색 요청을 전달하고 응답을 리스너에 위임한다")
    void searchByKeyset_delegates() throws Exception {
        // given
        PostSearchRepository repository = new PostSearchRepository(objectMapper, restHighLevelClient, indexRepository, "posts");
        ActionListener<List<PostSearch>> resultListener = mock(ActionListener.class);
        AtomicReference<SearchRequest> requestRef = new AtomicReference<>();
        AtomicReference<ActionListener<SearchResponse>> listenerRef = new AtomicReference<>();
        doAnswer(invocation -> {
            requestRef.set(invocation.getArgument(0));
            listenerRef.set(invocation.getArgument(2));
            return null;
        }).when(restHighLevelClient).searchAsync(any(SearchRequest.class), any(RequestOptions.class), any(ActionListener.class));
        SearchResponse response = mock(SearchResponse.class, RETURNS_DEEP_STUBS);
        when(response.getHits().getHits()).thenReturn(new SearchHit[0]);

        // when
        repository.searchByKeyset("hello", "10", 5, resultListener);

        // then
        SearchRequest request = requestRef.get();
        SearchSourceBuilder source = request.source();
        assertThat(request.indices()).contains("posts");
        assertThat(source.searchAfter()).containsExactly("10");
        assertThat(source.size()).isEqualTo(5);
        listenerRef.get().onResponse(response);
        verify(resultListener).onResponse(List.of());
        verify(restHighLevelClient).searchAsync(any(SearchRequest.class), eq(RequestOptions.DEFAULT), any(ActionListener.class));
    }

    @Test
    @DisplayName("오프셋 검색 요청을 전달하고 페이지 시작 값을 설정한다")
    void searchByOffset_delegates() throws Exception {
        // given
        PostSearchRepository repository = new PostSearchRepository(objectMapper, restHighLevelClient, indexRepository, "posts");
        ActionListener<List<PostSearch>> resultListener = mock(ActionListener.class);
        AtomicReference<SearchRequest> requestRef = new AtomicReference<>();
        AtomicReference<ActionListener<SearchResponse>> listenerRef = new AtomicReference<>();
        doAnswer(invocation -> {
            requestRef.set(invocation.getArgument(0));
            listenerRef.set(invocation.getArgument(2));
            return null;
        }).when(restHighLevelClient).searchAsync(any(SearchRequest.class), any(RequestOptions.class), any(ActionListener.class));
        SearchResponse response = mock(SearchResponse.class, RETURNS_DEEP_STUBS);
        when(response.getHits().getHits()).thenReturn(new SearchHit[0]);

        // when
        repository.searchByOffset("world", 2, 3, resultListener);

        // then
        SearchRequest request = requestRef.get();
        SearchSourceBuilder source = request.source();
        assertThat(source.from()).isEqualTo(3); // (page-1)*size
        assertThat(source.size()).isEqualTo(3);
        listenerRef.get().onResponse(response);
        verify(resultListener).onResponse(List.of());
        verify(restHighLevelClient).searchAsync(any(SearchRequest.class), eq(RequestOptions.DEFAULT), any(ActionListener.class));
    }
}
