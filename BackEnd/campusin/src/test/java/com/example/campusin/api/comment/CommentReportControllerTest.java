package com.example.campusin.api.comment;

import com.example.campusin.application.comment.CommentReportService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.example.campusin.support.MockMvcAuthSupport.authenticatedUser;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentReportController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CommentReportController")
class CommentReportControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CommentReportService commentReportService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("createReport 메서드는")
    class Describe_createReport {

        @Test
        @DisplayName("처음 신고면 성공 메시지를 반환한다")
        void creates_report() throws Exception {
            // given
            Long userId = 1L;
            Long commentId = 10L;
            given(commentReportService.createReport(userId, commentId)).willReturn(true);

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/comments/{id}/report", commentId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['댓글 신고 성공']").value("Comment Report Created Successfully"));
            verify(commentReportService).createReport(userId, commentId);
        }

        @Test
        @DisplayName("이미 신고한 댓글이면 안내 메시지를 반환한다")
        void already_reported() throws Exception {
            // given
            Long userId = 2L;
            Long commentId = 20L;
            given(commentReportService.createReport(userId, commentId)).willReturn(false);

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/comments/{id}/report", commentId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['이미 신고한 댓글입니다']").value("Already Reported Comment"));
            verify(commentReportService).createReport(userId, commentId);
        }
    }

    @Nested
    @DisplayName("deleteReport 메서드는")
    class Describe_deleteReport {

        @Test
        @DisplayName("신고가 존재하면 삭제한다")
        void deletes_report() throws Exception {
            // given
            Long userId = 3L;
            Long commentId = 30L;
            given(commentReportService.deleteReport(userId, commentId)).willReturn(true);

            // when
            ResultActions result = mockMvc.perform(delete("/api/v1/comments/{id}/report", commentId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['댓글 신고 삭제 성공']").value("Comment Report Deleted Successfully"));
            verify(commentReportService).deleteReport(userId, commentId);
        }

        @Test
        @DisplayName("신고하지 않은 댓글이면 안내 메시지를 반환한다")
        void not_reported() throws Exception {
            // given
            Long userId = 4L;
            Long commentId = 40L;
            given(commentReportService.deleteReport(userId, commentId)).willReturn(false);

            // when
            ResultActions result = mockMvc.perform(delete("/api/v1/comments/{id}/report", commentId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['신고하지 않은 댓글입니다']").value("Not Reported Comment"));
            verify(commentReportService).deleteReport(userId, commentId);
        }
    }
}
