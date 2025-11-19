package com.example.campusin.api.post;

import com.example.campusin.application.post.PostService;
import com.example.campusin.application.post.exception.PostNotFoundException;
import com.example.campusin.common.config.security.SecurityConfig;
import com.example.campusin.domain.post.ReportResult;
import com.example.campusin.domain.post.ReportType;
import com.example.campusin.domain.post.dto.request.PostUpdateRequest;
import com.example.campusin.domain.post.dto.response.PostIdResponse;
import com.example.campusin.domain.post.dto.response.PostResponse;
import com.example.campusin.domain.post.dto.response.PostSimpleResponse;
import com.example.campusin.domain.post.dto.response.PostStudyResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PostController")
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PostService postService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("update 메서드는")
    class Describe_update {

        @Test
        @DisplayName("유효한 요청이면 게시글 ID와 함께 성공 응답을 반환한다")
        void updates_post() throws Exception {
            // given
            Long postId = 1L;
            PostUpdateRequest request = PostUpdateRequest.builder()
                    .title("새 제목")
                    .content("내용")
                    .price(1000L)
                    .studyGroupId(2L)
                    .build();
            given(postService.updatePost(eq(postId), any(PostUpdateRequest.class)))
                    .willReturn(new PostIdResponse(postId));

            // when
            ResultActions result = mockMvc.perform(patch("/api/v1/posts/{postId}", postId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.header.code").value(200))
                    .andExpect(jsonPath("$.header.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.body['게시글 수정'].postId").value(postId));
            verify(postService).updatePost(eq(postId), any(PostUpdateRequest.class));
        }

        @Test
        @DisplayName("제목이 비어 있으면 400과 에러 코드를 반환한다")
        void blank_title() throws Exception {
            // given
            String invalidBody = """
                    {
                      "title": "",
                      "content": "내용"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(patch("/api/v1/posts/{postId}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidBody));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.code").value("C001"))
                    .andExpect(jsonPath("$.message").value("잘못된 요청 값입니다."))
                    .andExpect(jsonPath("$.errors[0].field").value("title"));
            verifyNoInteractions(postService);
        }
    }

    @Nested
    @DisplayName("showPost 메서드는")
    class Describe_showPost {

        @Test
        @DisplayName("게시글을 조회하면 상세 응답을 반환한다")
        void returns_detail() throws Exception {
            // given
            Long postId = 11L;
            PostResponse response = PostResponse.builder()
                    .postId(postId)
                    .userId(1L)
                    .title("제목")
                    .content("내용")
                    .boardType(null)
                    .photoList(List.of())
                    .createdAt(LocalDateTime.now())
                    .price(1000L)
                    .studyGroupId(2L)
                    .likeCount(0)
                    .reportCount(0)
                    .tagType(null)
                    .isBadgeAccepted(false)
                    .build();
            given(postService.readPost(postId)).willReturn(response);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/posts/{postId}", postId));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.header.code").value(200))
                    .andExpect(jsonPath("$.body['게시글 상세'].postId").value(postId))
                    .andExpect(jsonPath("$.body['게시글 상세'].title").value("제목"));
            verify(postService).readPost(postId);
        }

        @Test
        @DisplayName("게시글이 없으면 404와 에러 코드를 반환한다")
        void not_found() throws Exception {
            // given
            Long postId = 99L;
            given(postService.readPost(postId)).willThrow(new PostNotFoundException());

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/posts/{postId}", postId));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.code").value("P001"));
            verify(postService).readPost(postId);
        }
    }

    @Nested
    @DisplayName("delete 메서드는")
    class Describe_delete {

        @Test
        @DisplayName("게시글을 삭제하면 성공 메시지를 반환한다")
        void deletes_post() throws Exception {
            // given
            Long postId = 9L;

            // when
            ResultActions result = mockMvc.perform(delete("/api/v1/posts/{postId}", postId));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.header.code").value(200))
                    .andExpect(jsonPath("$.body['게시글 삭제']").value("Post deleted successfully"));
            verify(postService).deletePost(postId);
        }
    }

    @Nested
    @DisplayName("searchPosts 메서드는")
    class Describe_searchPosts {

        @Test
        @DisplayName("키워드로 검색 결과를 반환한다")
        void returns_search_results() throws Exception {
            // given
            PageRequest pageable = PageRequest.of(0, 5);
            PostSimpleResponse item = PostSimpleResponse.builder()
                    .postId(3L)
                    .title("검색 제목")
                    .content("검색 내용")
                    .createdAt(LocalDateTime.now())
                    .build();
            Page<PostSimpleResponse> page = new PageImpl<>(List.of(item), pageable, 1);
            given(postService.searchPosts(eq("키워드"), any(Pageable.class))).willReturn(page);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/posts")
                    .param("keyword", "키워드")
                    .param("page", "0")
                    .param("size", "5"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.header.code").value(200))
                    .andExpect(jsonPath("$.body['게시글 검색'].content[0].postId").value(3L));
            verify(postService).searchPosts(eq("키워드"), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("showMyPosts 메서드는")
    class Describe_showMyPosts {

        @Test
        @DisplayName("내 게시글 목록을 반환한다")
        void returns_my_posts() throws Exception {
            // given
            Long userId = 2L;
            PageRequest pageable = PageRequest.of(0, 3);
            PostSimpleResponse item = PostSimpleResponse.builder()
                    .postId(4L)
                    .title("내 글")
                    .createdAt(LocalDateTime.now())
                    .build();
            given(postService.getPostsByUser(eq(userId), any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of(item), pageable, 1));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/posts/mypost")
                    .with(authenticatedUser(userId))
                    .param("page", "0")
                    .param("size", "3"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.header.code").value(200))
                    .andExpect(jsonPath("$.body['게시글 목록'].content[0].postId").value(4L));
            verify(postService).getPostsByUser(eq(userId), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("showMyComments 메서드는")
    class Describe_showMyComments {

        @Test
        @DisplayName("내가 댓글 단 게시글 목록을 반환한다")
        void returns_commented_posts() throws Exception {
            // given
            Long userId = 6L;
            PageRequest pageable = PageRequest.of(0, 2);
            PostSimpleResponse item = PostSimpleResponse.builder()
                    .postId(12L)
                    .title("댓글 단 글")
                    .createdAt(LocalDateTime.now())
                    .build();
            given(postService.getPostsThatUserCommentedAt(eq(userId), any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of(item), pageable, 1));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/posts/mycomment")
                    .with(authenticatedUser(userId))
                    .param("page", "0")
                    .param("size", "2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.header.code").value(200))
                    .andExpect(jsonPath("$.body['내가 작성한 댓글의 게시글 목록'].content[0].postId").value(12L));
            verify(postService).getPostsThatUserCommentedAt(eq(userId), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("likePost 메서드는")
    class Describe_likePost {

        @Test
        @DisplayName("처음 좋아요를 누르면 성공 메시지를 반환한다")
        void like_successfully() throws Exception {
            // given
            Long userId = 5L;
            Long postId = 10L;
            given(postService.likePost(userId, postId)).willReturn(true);

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/posts/{postId}/like", postId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.header.code").value(200))
                    .andExpect(jsonPath("$.body['게시글 좋아요']").value("Post liked successfully"));
            verify(postService).likePost(userId, postId);
        }

        @Test
        @DisplayName("이미 좋아요한 게시글이면 다른 메시지를 반환한다")
        void already_liked() throws Exception {
            // given
            Long userId = 3L;
            Long postId = 7L;
            given(postService.likePost(userId, postId)).willReturn(false);

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/posts/{postId}/like", postId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.header.code").value(200))
                    .andExpect(jsonPath("$.body['이미 좋아요한 게시글입니다.']").value("Already liked post"));
            verify(postService).likePost(userId, postId);
        }
    }

    @Nested
    @DisplayName("unlikePost 메서드는")
    class Describe_unlikePost {

        @Test
        @DisplayName("좋아요 취소하면 성공 메시지를 반환한다")
        void unlikes_post() throws Exception {
            // given
            Long userId = 1L;
            Long postId = 2L;
            given(postService.unlikePost(userId, postId)).willReturn(true);

            // when
            ResultActions result = mockMvc.perform(delete("/api/v1/posts/{postId}/like", postId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['게시글 좋아요 취소']").value("Post unliked successfully"));
            verify(postService).unlikePost(userId, postId);
        }

        @Test
        @DisplayName("좋아요하지 않은 게시글이면 안내 메시지를 반환한다")
        void not_liked_post() throws Exception {
            // given
            Long userId = 1L;
            Long postId = 2L;
            given(postService.unlikePost(userId, postId)).willReturn(false);

            // when
            ResultActions result = mockMvc.perform(delete("/api/v1/posts/{postId}/like", postId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['좋아요를 누르지 않은 게시글입니다.']").value("Not liked post"));
            verify(postService).unlikePost(userId, postId);
        }
    }

    @Nested
    @DisplayName("reportPost 메서드는")
    class Describe_reportPost {

        @Test
        @DisplayName("신고 누적으로 숨김 처리되면 안내 메시지를 반환한다")
        void hides_post_when_threshold_exceeded() throws Exception {
            // given
            Long userId = 4L;
            Long postId = 8L;
            given(postService.reportPost(userId, postId, ReportType.SPAM))
                    .willReturn(ReportResult.POST_HIDDEN);

            String requestBody = """
                    {
                      "type": "SPAM"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/posts/{postId}/report", postId)
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.header.code").value(200))
                    .andExpect(jsonPath("$.body['게시글이 신고로 숨겨졌습니다.']").value("Post hidden due to reports"));
            verify(postService).reportPost(userId, postId, ReportType.SPAM);
        }

        @Test
        @DisplayName("신고를 처음 하면 성공 메시지를 반환한다")
        void reports_successfully() throws Exception {
            // given
            Long userId = 2L;
            Long postId = 13L;
            given(postService.reportPost(userId, postId, ReportType.ABUSE))
                    .willReturn(ReportResult.SUCCESS);

            String body = """
                    {
                      "type": "ABUSE"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/posts/{postId}/report", postId)
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['게시글 신고']").value("Post reported successfully"));
            verify(postService).reportPost(userId, postId, ReportType.ABUSE);
        }

        @Test
        @DisplayName("이미 신고한 게시글이면 안내 메시지를 반환한다")
        void already_reported() throws Exception {
            // given
            Long userId = 2L;
            Long postId = 13L;
            given(postService.reportPost(userId, postId, ReportType.ETC))
                    .willReturn(ReportResult.ALREADY_REPORTED);

            String body = """
                    {
                      "type": "ETC"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/posts/{postId}/report", postId)
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['이미 신고한 게시글입니다.']").value("Already reported post"));
            verify(postService).reportPost(userId, postId, ReportType.ETC);
        }
    }

    @Nested
    @DisplayName("unreportPost 메서드는")
    class Describe_unreportPost {

        @Test
        @DisplayName("신고 취소하면 성공 메시지를 반환한다")
        void unreports_post() throws Exception {
            // given
            Long userId = 9L;
            Long postId = 10L;
            given(postService.unreportPost(userId, postId)).willReturn(true);

            // when
            ResultActions result = mockMvc.perform(delete("/api/v1/posts/{postId}/report", postId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['게시글 신고 취소']").value("Post unreported successfully"));
            verify(postService).unreportPost(userId, postId);
        }

        @Test
        @DisplayName("신고 내역이 없으면 안내 메시지를 반환한다")
        void not_reported_post() throws Exception {
            // given
            Long userId = 9L;
            Long postId = 10L;
            given(postService.unreportPost(userId, postId)).willReturn(false);

            // when
            ResultActions result = mockMvc.perform(delete("/api/v1/posts/{postId}/report", postId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['신고하지 않은 게시글입니다.']").value("Not reported post"));
            verify(postService).unreportPost(userId, postId);
        }
    }

    @Nested
    @DisplayName("getPostsByStudyGroupId 메서드는")
    class Describe_getPostsByStudyGroupId {

        @Test
        @DisplayName("스터디 그룹의 게시글 목록을 반환한다")
        void returns_study_group_posts() throws Exception {
            // given
            Long studyGroupId = 15L;
            PageRequest pageable = PageRequest.of(0, 2);
            PostStudyResponse response = PostStudyResponse.builder()
                    .postId(21L)
                    .userId(5L)
                    .nickname("nick")
                    .title("스터디 글")
                    .content("스터디 내용")
                    .createdAt(LocalDateTime.now())
                    .studyGroupId(studyGroupId)
                    .build();
            given(postService.getPostsByStudyGroup(eq(studyGroupId), any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of(response), pageable, 1));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/posts/{studyGroupId}/posts", studyGroupId)
                    .param("page", "0")
                    .param("size", "2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.header.code").value(200))
                    .andExpect(jsonPath("$.body['게시글 목록'].content[0].studyGroupId").value(studyGroupId));
            verify(postService).getPostsByStudyGroup(eq(studyGroupId), any(Pageable.class));
        }
    }

}
