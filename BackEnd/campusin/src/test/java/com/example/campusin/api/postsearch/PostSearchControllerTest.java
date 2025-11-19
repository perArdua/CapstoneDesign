package com.example.campusin.api.postsearch;

import com.example.campusin.application.postsearch.PostSearchService;
import com.example.campusin.common.config.security.SecurityConfig;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.postsearch.dto.response.PostSearchResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.opensearch.action.ActionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostSearchController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PostSearchController")
class PostSearchControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostSearchService postSearchService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("search 메서드는")
    class Describe_search {

        @Test
        @DisplayName("키워드 검색 결과를 반환한다")
        void returns_search_results() throws Exception {
            // given
            doAnswer(invocation -> {
                ActionListener<List<PostSearchResponse>> listener = invocation.getArgument(3);
                listener.onResponse(List.of(PostSearchResponse.builder()
                        .id("1")
                        .title("제목")
                        .content("내용")
                        .boardId(1L)
                        .userId(1L)
                        .build()));
                return null;
            }).when(postSearchService).searchWithKeysetPagination(anyString(), any(), anyInt(), any());

            // when
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post-search/search")
                            .param("keyword", "test")
                            .param("size", "10"))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            // then
            mockMvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.body.data[0].title").value("제목"));
        }

        @Test
        @DisplayName("검색 실패 시 500을 반환한다")
        void returns_failure_on_error() throws Exception {
            // given
            doAnswer(invocation -> {
                ActionListener<List<PostSearchResponse>> listener = invocation.getArgument(3);
                listener.onFailure(new RuntimeException("fail"));
                return null;
            }).when(postSearchService).searchWithKeysetPagination(anyString(), any(), anyInt(), any());

            // when
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post-search/search")
                            .param("keyword", "test")
                            .param("size", "10"))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            // then
            mockMvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.header.code").value(500));
        }
    }

    @Nested
    @DisplayName("searchByOffset 메서드는")
    class Describe_searchByOffset {

        @Test
        @DisplayName("오프셋 검색 결과를 반환한다")
        void returns_offset_results() throws Exception {
            // given
            doAnswer(invocation -> {
                ActionListener<List<PostSearchResponse>> listener = invocation.getArgument(3);
                listener.onResponse(List.of(PostSearchResponse.builder()
                        .id("2")
                        .title("offset")
                        .content("내용")
                        .boardId(1L)
                        .userId(1L)
                        .build()));
                return null;
            }).when(postSearchService).searchByOffset(anyString(), anyInt(), anyInt(), any());

            // when
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post-search/search-offset")
                            .param("keyword", "test")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            // then
            mockMvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.body.data[0].title").value("offset"));
        }

        @Test
        @DisplayName("오프셋 검색 실패 시 500을 반환한다")
        void returns_failure_on_error() throws Exception {
            // given
            doAnswer(invocation -> {
                ActionListener<List<PostSearchResponse>> listener = invocation.getArgument(3);
                listener.onFailure(new RuntimeException("fail"));
                return null;
            }).when(postSearchService).searchByOffset(anyString(), anyInt(), anyInt(), any());

            // when
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post-search/search-offset")
                            .param("keyword", "test")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            // then
            mockMvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.header.code").value(500));
            assertThat(mvcResult.getResponse().getContentAsString()).contains("\"code\"");
        }
    }

    @Nested
    @DisplayName("reindexAll 메서드는")
    class Describe_reindexAll {

        @Test
        @DisplayName("재색인 요청을 접수한다(202 반환)")
        void reindexes_all() throws Exception {
            // when // then
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/post-search/reindex-all"))
                    .andExpect(status().isAccepted())
                    .andExpect(jsonPath("$.body.data").value("재색인 요청이 접수되었습니다."));
        }
    }
}
