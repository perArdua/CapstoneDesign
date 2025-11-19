package com.example.campusin.application.postsearch;

import com.example.campusin.domain.postsearch.PostSearch;
import com.example.campusin.domain.postsearch.dto.response.PostSearchResponse;
import com.example.campusin.infra.postsearch.PostSearchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.action.ActionListener;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostSearchService")
class PostSearchServiceTest {

    @Mock
    PostSearchRepository postSearchRepository;
    @Mock
    PostReindexer postReindexer;

    @InjectMocks
    PostSearchService postSearchService;

    @Nested
    @DisplayName("searchWithKeysetPagination 메서드는")
    class Describe_searchWithKeysetPagination {

        @Nested
        @DisplayName("검색이 성공하면")
        class Context_when_success {

            @Test
            @DisplayName("조회 결과를 PostSearchResponse로 변환해 리스너 onResponse에 전달한다")
            void 결과를_변환해_리스너에_전달한다() {
                // given
                String keyword = "자료구조";
                String lastSortValue = "100";
                int size = 3;
                ActionListener<List<PostSearchResponse>> listener = mock(ActionListener.class);
                ArgumentCaptor<ActionListener<List<PostSearch>>> repositoryListener = ArgumentCaptor.forClass(ActionListener.class);

                // when
                postSearchService.searchWithKeysetPagination(keyword, lastSortValue, size, listener);

                // then
                verify(postSearchRepository).searchByKeyset(eq(keyword), eq(lastSortValue), eq(size), repositoryListener.capture());

                List<PostSearch> posts = List.of(
                        PostSearch.builder().id("1").title("t1").content("c1").boardId(10L).userId(20L).build(),
                        PostSearch.builder().id("2").title("t2").content("c2").boardId(11L).userId(21L).build()
                );

                repositoryListener.getValue().onResponse(posts);

                ArgumentCaptor<List<PostSearchResponse>> responseCaptor = ArgumentCaptor.forClass(List.class);
                verify(listener).onResponse(responseCaptor.capture());
                List<PostSearchResponse> responses = responseCaptor.getValue();
                assertThat(responses).hasSize(2);
                assertThat(responses.get(0).getId()).isEqualTo("1");
                assertThat(responses.get(1).getBoardId()).isEqualTo(11L);
                verify(listener, never()).onFailure(any());
            }
        }

        @Nested
        @DisplayName("검색이 실패하면")
        class Context_when_failure {

            @Test
            @DisplayName("리스너 onFailure를 호출한다")
            void 실패를_전달한다() {
                // given
                ActionListener<List<PostSearchResponse>> listener = mock(ActionListener.class);
                ArgumentCaptor<ActionListener<List<PostSearch>>> repositoryListener = ArgumentCaptor.forClass(ActionListener.class);
                Exception failure = new RuntimeException("search fail");

                postSearchService.searchWithKeysetPagination("k", null, 2, listener);
                verify(postSearchRepository).searchByKeyset(eq("k"), eq(null), eq(2), repositoryListener.capture());

                // when
                repositoryListener.getValue().onFailure(failure);

                // then
                verify(listener, never()).onResponse(any());
                verify(listener).onFailure(failure);
            }

            @Test
            @DisplayName("리스너 onResponse가 예외를 던지면 onFailure로 포워딩한다")
            void 변환중_예외를_전달한다() {
                // given
                @SuppressWarnings("unchecked")
                ActionListener<List<PostSearchResponse>> listener = mock(ActionListener.class);
                ArgumentCaptor<ActionListener<List<PostSearch>>> repositoryListener = ArgumentCaptor.forClass(ActionListener.class);
                RuntimeException failure = new RuntimeException("listener failed");

                postSearchService.searchWithKeysetPagination("k", "1", 1, listener);
                verify(postSearchRepository).searchByKeyset(eq("k"), eq("1"), eq(1), repositoryListener.capture());
                doThrow(failure).when(listener).onResponse(any());

                // when
                repositoryListener.getValue().onResponse(List.of(PostSearch.builder().id("1").build()));

                // then
                verify(listener).onFailure(failure);
            }
        }
    }

    @Nested
    @DisplayName("searchByOffset 메서드는")
    class Describe_searchByOffset {

        @Nested
        @DisplayName("검색이 성공하면")
        class Context_when_success {

            @Test
            @DisplayName("조회 결과를 PostSearchResponse로 변환해 리스너 onResponse에 전달한다")
            void 오프셋_결과를_전달한다() {
                // given
                String keyword = "운영체제";
                int page = 2;
                int size = 5;
                ActionListener<List<PostSearchResponse>> listener = mock(ActionListener.class);
                ArgumentCaptor<ActionListener<List<PostSearch>>> repositoryListener = ArgumentCaptor.forClass(ActionListener.class);

                // when
                postSearchService.searchByOffset(keyword, page, size, listener);

                // then
                verify(postSearchRepository).searchByOffset(eq(keyword), eq(page), eq(size), repositoryListener.capture());

                List<PostSearch> posts = List.of(PostSearch.builder().id("a").title("title").content("content").boardId(1L).userId(2L).build());
                repositoryListener.getValue().onResponse(posts);

                ArgumentCaptor<List<PostSearchResponse>> responseCaptor = ArgumentCaptor.forClass(List.class);
                verify(listener).onResponse(responseCaptor.capture());
                List<PostSearchResponse> responses = responseCaptor.getValue();
                assertThat(responses).hasSize(1);
                assertThat(responses.get(0).getContent()).isEqualTo("content");
                verify(listener, never()).onFailure(any());
            }
        }

        @Nested
        @DisplayName("검색이 실패하면")
        class Context_when_failure {

            @Test
            @DisplayName("리스너 onFailure를 호출한다")
            void 실패를_전달한다() {
                // given
                ActionListener<List<PostSearchResponse>> listener = mock(ActionListener.class);
                ArgumentCaptor<ActionListener<List<PostSearch>>> repositoryListener = ArgumentCaptor.forClass(ActionListener.class);
                Exception failure = new IllegalStateException("offset fail");

                postSearchService.searchByOffset("key", 1, 3, listener);
                verify(postSearchRepository).searchByOffset(eq("key"), eq(1), eq(3), repositoryListener.capture());

                // when
                repositoryListener.getValue().onFailure(failure);

                // then
                verify(listener, never()).onResponse(any());
                verify(listener).onFailure(failure);
            }
        }
    }

    @Nested
    @DisplayName("reindexAllAsync 메서드는")
    class Describe_reindexAllAsync {

        @Test
        @DisplayName("PostReindexer를 호출한다")
        void 리인덱스를_호출한다() {
            // when
            postSearchService.reindexAllAsync();

            // then
            verify(postReindexer).reindexAll();
            verifyNoInteractions(postSearchRepository);
        }
    }
}
