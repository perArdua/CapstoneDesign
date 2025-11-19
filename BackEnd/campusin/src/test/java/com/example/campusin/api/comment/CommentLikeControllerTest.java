package com.example.campusin.api.comment;

import com.example.campusin.application.comment.CommentLikeService;
import com.example.campusin.common.config.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.example.campusin.support.MockMvcAuthSupport.authenticatedUser;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentLikeController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CommentLikeController")
class CommentLikeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CommentLikeService commentLikeService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("createLike 메서드는")
    class Describe_createLike {

        @Test
        @DisplayName("처음 좋아요면 201을 반환한다")
        void creates_like() throws Exception {
            // given
            Long userId = 1L;
            Long commentId = 10L;
            given(commentLikeService.createLike(userId, commentId)).willReturn(true);

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/comments/{id}/like", commentId)
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.body['댓글 좋아요 생성 성공']").value("Comment Like Created Successfully"));
            verify(commentLikeService).createLike(userId, commentId);
        }

        @Test
        @DisplayName("이미 좋아요를 눌렀다면 200과 안내 메시지를 반환한다")
        void returns_already_liked_message() throws Exception {
            // given
            Long userId = 2L;
            Long commentId = 20L;
            given(commentLikeService.createLike(userId, commentId)).willReturn(false);

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/comments/{id}/like", commentId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['이미 좋아요를 누른 댓글입니다']").value("Already Liked Comment"));
            verify(commentLikeService).createLike(userId, commentId);
        }
    }

    @Nested
    @DisplayName("deleteLike 메서드는")
    class Describe_deleteLike {

        @Test
        @DisplayName("좋아요가 존재하면 삭제하고 성공 메시지를 반환한다")
        void deletes_like() throws Exception {
            // given
            Long userId = 3L;
            Long commentId = 30L;
            given(commentLikeService.deleteLike(userId, commentId)).willReturn(true);

            // when
            ResultActions result = mockMvc.perform(delete("/api/v1/comments/{id}/like", commentId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['댓글 좋아요 삭제 성공']").value("Comment Like Deleted Successfully"));
            verify(commentLikeService).deleteLike(userId, commentId);
        }

        @Test
        @DisplayName("좋아요를 누르지 않았다면 안내 메시지를 반환한다")
        void returns_not_liked_message() throws Exception {
            // given
            Long userId = 4L;
            Long commentId = 40L;
            given(commentLikeService.deleteLike(userId, commentId)).willReturn(false);

            // when
            ResultActions result = mockMvc.perform(delete("/api/v1/comments/{id}/like", commentId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['좋아요를 누르지 않은 댓글입니다']").value("Not Liked Comment"));
            verify(commentLikeService).deleteLike(userId, commentId);
        }
    }
}
