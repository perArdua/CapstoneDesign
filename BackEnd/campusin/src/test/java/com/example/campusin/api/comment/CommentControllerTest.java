package com.example.campusin.api.comment;

import com.example.campusin.application.comment.CommentService;
import com.example.campusin.application.comment.exception.CommentNotFoundException;
import com.example.campusin.application.post.exception.PostNotFoundException;
import com.example.campusin.common.config.security.SecurityConfig;
import com.example.campusin.domain.comment.dto.response.CommentCreateResponse;
import com.example.campusin.domain.comment.dto.response.CommentsOnPostResponse;
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

import java.util.List;

import static com.example.campusin.support.MockMvcAuthSupport.authenticatedUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CommentController")
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CommentService commentService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("create 메서드는")
    class Describe_create {

        @Test
        @DisplayName("댓글을 생성하고 Location 헤더와 응답을 반환한다")
        void creates_comment() throws Exception {
            // given
            Long userId = 3L;
            Long postId = 10L;
            String requestBody = """
                    {
                      "content": "댓글입니다"
                    }
                    """;
            CommentCreateResponse response = CommentCreateResponse.builder()
                    .commentId(5L)
                    .content("댓글입니다")
                    .userId(userId)
                    .nickname("사용자")
                    .build();
            given(commentService.createComment(eq(userId), any(), eq(postId)))
                    .willReturn(response);

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/posts/{postId}/comments", postId)
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/v1/posts/" + postId))
                    .andExpect(jsonPath("$.body['댓글 생성 success'].commentId").value(5L));
            verify(commentService).createComment(eq(userId), any(), eq(postId));
        }

        @Test
        @DisplayName("내용이 비어 있으면 400을 반환한다")
        void blank_content() throws Exception {
            // given
            String body = """
                    {
                      "content": ""
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/posts/{postId}/comments", 1L)
                    .with(authenticatedUser(1L))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("C001"))
                    .andExpect(jsonPath("$.errors[0].field").value("content"));
            verifyNoInteractions(commentService);
        }

        @Test
        @DisplayName("게시글이 없으면 404를 반환한다")
        void post_not_found() throws Exception {
            // given
            Long postId = 99L;
            String body = """
                    {
                      "content": "댓글"
                    }
                    """;
            given(commentService.createComment(eq(1L), any(), eq(postId)))
                    .willThrow(new PostNotFoundException());

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/posts/{postId}/comments", postId)
                    .with(authenticatedUser(1L))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("P001"));
            verify(commentService).createComment(eq(1L), any(), eq(postId));
        }
    }

    @Nested
    @DisplayName("deleteComment 메서드는")
    class Describe_deleteComment {

        @Test
        @DisplayName("댓글을 삭제하면 성공 메시지를 반환한다")
        void deletes_comment() throws Exception {
            // given
            Long userId = 2L;
            Long commentId = 7L;

            // when
            ResultActions result = mockMvc.perform(delete("/api/v1/posts/{postId}/comments/{commentId}", 1L, commentId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['댓글 삭제 성공']").value("COMMENT DELETE SUCCESSFULLY"));
            verify(commentService).deleteComment(userId, commentId);
        }

        @Test
        @DisplayName("댓글이 없으면 404를 반환한다")
        void comment_not_found() throws Exception {
            // given
            Long userId = 2L;
            Long commentId = 7L;
            doThrow(new CommentNotFoundException()).when(commentService).deleteComment(userId, commentId);

            // when
            ResultActions result = mockMvc.perform(delete("/api/v1/posts/{postId}/comments/{commentId}", 1L, commentId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("P002"));
            verify(commentService).deleteComment(userId, commentId);
        }
    }

    @Nested
    @DisplayName("searchComments 메서드는")
    class Describe_searchComments {

        @Test
        @DisplayName("게시글의 댓글 목록을 반환한다")
        void returns_comments() throws Exception {
            // given
            Long postId = 4L;
            PageRequest pageable = PageRequest.of(0, 3);
            CommentsOnPostResponse item = new CommentsOnPostResponse(
                    1L, null, 1L, 0, 0, "닉", "댓글", false, 1L, postId
            );
            Page<CommentsOnPostResponse> page = new PageImpl<>(List.of(item), pageable, 1);
            given(commentService.searchCommentByPost(eq(postId), any(Pageable.class))).willReturn(page);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/posts/{postId}/comments", postId)
                    .param("page", "0")
                    .param("size", "3"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['댓글 조회 성공'].content[0].commentId").value(1L));
            verify(commentService).searchCommentByPost(eq(postId), any(Pageable.class));
        }

        @Test
        @DisplayName("게시글이 없으면 404를 반환한다")
        void post_not_found() throws Exception {
            // given
            Long postId = 4L;
            given(commentService.searchCommentByPost(eq(postId), any(Pageable.class)))
                    .willThrow(new PostNotFoundException());

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/posts/{postId}/comments", postId));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("P001"));
            verify(commentService).searchCommentByPost(eq(postId), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("acceptAnswer 메서드는")
    class Describe_acceptAnswer {

        @Test
        @DisplayName("답변을 채택하면 성공 메시지를 반환한다")
        void accepts_answer() throws Exception {
            // given
            Long userId = 1L;
            Long commentId = 8L;

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/posts/{postId}/comments/{commentId}/accept", 1L, commentId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['답변 채택 성공']").value("ACCEPT ANSWER SUCCESSFULLY"));
            verify(commentService).updateIsAdopted(commentId, userId);
        }

        @Test
        @DisplayName("댓글이 없으면 404를 반환한다")
        void comment_not_found() throws Exception {
            // given
            Long userId = 1L;
            Long commentId = 8L;
            doThrow(new CommentNotFoundException()).when(commentService).updateIsAdopted(commentId, userId);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/posts/{postId}/comments/{commentId}/accept", 1L, commentId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("P002"));
            verify(commentService).updateIsAdopted(commentId, userId);
        }
    }
}
