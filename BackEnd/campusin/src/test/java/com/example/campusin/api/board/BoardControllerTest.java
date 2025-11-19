package com.example.campusin.api.board;

import com.example.campusin.application.post.PostService;
import com.example.campusin.application.post.exception.BoardNotFoundException;
import com.example.campusin.application.post.exception.TagNotFoundException;
import com.example.campusin.common.config.security.SecurityConfig;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.board.dto.response.BoardSimpleResponse;
import com.example.campusin.domain.post.dto.request.PostCreateRequest;
import com.example.campusin.domain.post.dto.response.PostIdResponse;
import com.example.campusin.domain.post.dto.response.PostSimpleResponse;
import com.example.campusin.domain.post.dto.response.PostStudyResponse;
import com.example.campusin.domain.tag.TagType;
import com.example.campusin.domain.tag.dto.response.TagResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.campusin.support.MockMvcAuthSupport.authenticatedUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BoardController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("BoardController")
class BoardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PostService postService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("showPostsByBoard 메서드는")
    class Describe_showPostsByBoard {

        @Test
        @DisplayName("게시글 목록을 반환한다")
        void returns_posts() throws Exception {
            // given
            Long boardId = 1L;
            PageRequest pageable = PageRequest.of(0, 5);
            PostSimpleResponse item = PostSimpleResponse.builder()
                    .postId(10L)
                    .title("게시글")
                    .createdAt(LocalDateTime.now())
                    .build();
            Page<PostSimpleResponse> page = new PageImpl<>(List.of(item), pageable, 1);
            given(postService.getPostsByBoard(eq(boardId), any(Pageable.class))).willReturn(page);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/boards/{boardId}/posts", boardId)
                    .param("page", "0")
                    .param("size", "5"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.header.code").value(200))
                    .andExpect(jsonPath("$.body['게시글 목록'].content[0].postId").value(10L));
            verify(postService).getPostsByBoard(eq(boardId), any(Pageable.class));
        }

        @Test
        @DisplayName("게시판이 없으면 404를 반환한다")
        void board_not_found() throws Exception {
            // given
            Long boardId = 2L;
            given(postService.getPostsByBoard(eq(boardId), any(Pageable.class)))
                    .willThrow(new BoardNotFoundException());

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/boards/{boardId}/posts", boardId));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("P005"));
            verify(postService).getPostsByBoard(eq(boardId), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("searchPostsAtBoard 메서드는")
    class Describe_searchPostsAtBoard {

        @Test
        @DisplayName("키워드로 게시판 내 검색 결과를 반환한다")
        void returns_search_results() throws Exception {
            // given
            Long boardId = 3L;
            PageRequest pageable = PageRequest.of(0, 3);
            PostSimpleResponse item = PostSimpleResponse.builder()
                    .postId(7L)
                    .title("검색 글")
                    .createdAt(LocalDateTime.now())
                    .build();
            given(postService.searchPostsAtBoard(eq(boardId), eq("검색"), any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of(item), pageable, 1));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/boards/{boardId}/posts/search", boardId)
                    .param("keyword", "검색")
                    .param("page", "0")
                    .param("size", "3"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['게시글 목록'].content[0].postId").value(7L));
            verify(postService).searchPostsAtBoard(eq(boardId), eq("검색"), any(Pageable.class));
        }

        @Test
        @DisplayName("게시판이 없으면 404를 반환한다")
        void board_not_found() throws Exception {
            // given
            Long boardId = 3L;
            given(postService.searchPostsAtBoard(eq(boardId), eq("검색"), any(Pageable.class)))
                    .willThrow(new BoardNotFoundException());

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/boards/{boardId}/posts/search", boardId)
                    .param("keyword", "검색"));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("P005"));
            verify(postService).searchPostsAtBoard(eq(boardId), eq("검색"), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("createPost 메서드는")
    class Describe_createPost {

        @Test
        @DisplayName("유효한 요청이면 생성된 게시글 ID를 반환한다")
        void creates_post() throws Exception {
            // given
            Long boardId = 1L;
            Long tagId = 2L;
            Long userId = 5L;
            PostCreateRequest request = PostCreateRequest.builder()
                    .title("제목")
                    .content("내용")
                    .price(1000L)
                    .build();
            given(postService.createPost(eq(boardId), eq(tagId), eq(userId), any(PostCreateRequest.class)))
                    .willReturn(new PostIdResponse(101L));

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/boards/{boardId}/posts/{tagId}", boardId, tagId)
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['게시글 생성'].postId").value(101L));
            verify(postService).createPost(eq(boardId), eq(tagId), eq(userId), any(PostCreateRequest.class));
        }

        @Test
        @DisplayName("제목이 비어 있으면 400을 반환한다")
        void invalid_title() throws Exception {
            // given
            String body = """
                    {
                      "title": "",
                      "content": "내용"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/boards/{boardId}/posts/{tagId}", 1L, 2L)
                    .with(authenticatedUser(1L))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("C001"))
                    .andExpect(jsonPath("$.errors[0].field").value("title"));
            verifyNoInteractions(postService);
        }

        @Test
        @DisplayName("태그가 없으면 404를 반환한다")
        void tag_not_found() throws Exception {
            // given
            Long boardId = 1L;
            Long tagId = 9L;
            Long userId = 5L;
            PostCreateRequest request = PostCreateRequest.builder()
                    .title("제목")
                    .content("내용")
                    .build();
            given(postService.createPost(eq(boardId), eq(tagId), eq(userId), any(PostCreateRequest.class)))
                    .willThrow(new TagNotFoundException());

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/boards/{boardId}/posts/{tagId}", boardId, tagId)
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("P006"));
            verify(postService).createPost(eq(boardId), eq(tagId), eq(userId), any(PostCreateRequest.class));
        }
    }

    @Nested
    @DisplayName("initBoard 메서드는")
    class Describe_initBoard {

        @Test
        @DisplayName("초기화가 수행되면 true를 반환한다")
        void init_success() throws Exception {
            // given
            given(postService.initBoard()).willReturn(true);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/boards/init"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['게시판, 태그 초기화']").value(true));
            verify(postService).initBoard();
        }

        @Test
        @DisplayName("이미 초기화되어 있으면 false를 반환한다")
        void already_initialized() throws Exception {
            // given
            given(postService.initBoard()).willReturn(false);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/boards/init"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['게시판, 태그 초기화']").value(false));
            verify(postService).initBoard();
        }
    }

    @Nested
    @DisplayName("getBoardIds 메서드는")
    class Describe_getBoardIds {

        @Test
        @DisplayName("게시판 ID 목록을 반환한다")
        void returns_board_ids() throws Exception {
            // given
            PageRequest pageable = PageRequest.of(0, 2);
            BoardSimpleResponse resp = new BoardSimpleResponse(1L, BoardType.Free);
            given(postService.getBoardIds(any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of(resp), pageable, 1));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/boards/boards/ids")
                    .param("page", "0")
                    .param("size", "2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['게시판 고유 id값 얻기'].content[0].boardId").value(1L));
            verify(postService).getBoardIds(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("getTagIds 메서드는")
    class Describe_getTagIds {

        @Test
        @DisplayName("태그 ID 목록을 반환한다")
        void returns_tag_ids() throws Exception {
            // given
            PageRequest pageable = PageRequest.of(0, 2);
            TagResponse resp = new TagResponse(4L, TagType.IT);
            given(postService.getTags(any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of(resp), pageable, 1));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/boards/tags/ids")
                    .param("page", "0")
                    .param("size", "2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['태그별 고유 id값 얻기'].content[0].tagId").value(4L));
            verify(postService).getTags(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("showPostsByTag 메서드는")
    class Describe_showPostsByTag {

        @Test
        @DisplayName("태그별 게시글 목록을 반환한다")
        void returns_posts_by_tag() throws Exception {
            // given
            Long boardId = 1L;
            Long tagId = 2L;
            PageRequest pageable = PageRequest.of(0, 3);
            PostSimpleResponse item = PostSimpleResponse.builder()
                    .postId(30L)
                    .title("태그 글")
                    .createdAt(LocalDateTime.now())
                    .build();
            given(postService.getPostsByTag(eq(boardId), eq(tagId), any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of(item), pageable, 1));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/boards/tag/{boardId}/{tagId}/posts", boardId, tagId)
                    .param("page", "0")
                    .param("size", "3"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['게시글 목록'].content[0].postId").value(30L));
            verify(postService).getPostsByTag(eq(boardId), eq(tagId), any(Pageable.class));
        }

        @Test
        @DisplayName("태그가 없으면 404를 반환한다")
        void tag_not_found() throws Exception {
            // given
            Long boardId = 1L;
            Long tagId = 99L;
            given(postService.getPostsByTag(eq(boardId), eq(tagId), any(Pageable.class)))
                    .willThrow(new TagNotFoundException());

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/boards/tag/{boardId}/{tagId}/posts", boardId, tagId));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("P006"));
            verify(postService).getPostsByTag(eq(boardId), eq(tagId), any(Pageable.class));
        }
    }
}
