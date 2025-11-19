package com.example.campusin.api.admin;

import com.example.campusin.application.badge.BadgeService;
import com.example.campusin.application.comment.CommentService;
import com.example.campusin.application.post.PostService;
import com.example.campusin.common.config.security.SecurityConfig;
import com.example.campusin.domain.badge.request.BadgeCreateRequest;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.board.dto.response.BoardSimpleResponse;
import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.comment.dto.response.CommentsOnPostResponse;
import com.example.campusin.domain.post.dto.response.PostSimpleResponse;
import com.example.campusin.domain.badge.Badge;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.example.campusin.support.MockMvcAuthSupport.authenticatedUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminController")
class AdminControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PostService postService;

    @MockBean
    CommentService commentService;

    @MockBean
    BadgeService badgeService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("showRequestBadge 메서드는")
    class Describe_showRequestBadge {

        @Test
        @DisplayName("뱃지 수락 게시판 글 목록을 반환한다")
        void returns_badge_requests() throws Exception {
            // given
            BoardSimpleResponse badgeBoard = new BoardSimpleResponse(5L, BoardType.AdminBadgeAccept);
            Page<BoardSimpleResponse> boards = new PageImpl<>(List.of(badgeBoard), PageRequest.of(0, 10), 1);
            Page<PostSimpleResponse> posts = new PageImpl<>(List.of(PostSimpleResponse.builder()
                    .postId(1L)
                    .title("배지 요청")
                    .reportCount(0)
                    .build()));

            when(postService.getBoardIds(any(Pageable.class))).thenReturn(boards);
            when(postService.getPostsByBoard(eq(5L), any(Pageable.class))).thenReturn(posts);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/admin/badge")
                    .param("page", "0")
                    .param("size", "10"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['뱃지 요청 게시글 목록'].content[0].postId").value(1L));
            verify(postService).getBoardIds(any(Pageable.class));
            verify(postService).getPostsByBoard(eq(5L), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("showReportedPost 메서드는")
    class Describe_showReportedPost {

        @Test
        @DisplayName("신고된 게시글 목록을 반환한다")
        void returns_reported_posts() throws Exception {
            // given
            BoardSimpleResponse board = new BoardSimpleResponse(1L, BoardType.Free);
            when(postService.getBoardIds(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(board), PageRequest.of(0, 10), 1));

            PostSimpleResponse reported = PostSimpleResponse.builder()
                    .postId(3L)
                    .title("신고된 글")
                    .reportCount(2)
                    .build();
            Page<PostSimpleResponse> posts = new PageImpl<>(List.of(reported));
            when(postService.getPostsByBoard(eq(1L), any(Pageable.class))).thenReturn(posts);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/admin/post")
                    .param("page", "0")
                    .param("size", "10"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['게시글 목록'][0].postId").value(3L));
            verify(postService).getPostsByBoard(eq(1L), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("showReportComment 메서드는")
    class Describe_showReportComment {

        @Test
        @DisplayName("신고된 댓글 목록을 반환한다")
        void returns_reported_comments() throws Exception {
            // given (빈 결과라도 성공 응답 확인)
            when(commentService.getAllComments(any(Pageable.class)))
                    .thenReturn(Page.empty());

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/admin/comment")
                    .param("page", "0")
                    .param("size", "10"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['댓글 조회 성공']").isArray());
            verify(commentService).getAllComments(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("blockPost 메서드는")
    class Describe_blockPost {
        @Test
        @DisplayName("게시글을 차단한다")
        void blocks_post() throws Exception {
            Long postId = 10L;

            ResultActions result = mockMvc.perform(get("/api/v1/admin/block/post/{postId}", postId));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['게시글 차단']").value("게시글 차단 완료"));
            verify(postService).blockPost(postId);
        }
    }

    @Nested
    @DisplayName("blockComment 메서드는")
    class Describe_blockComment {
        @Test
        @DisplayName("댓글을 차단한다")
        void blocks_comment() throws Exception {
            Long commentId = 3L;

            ResultActions result = mockMvc.perform(get("/api/v1/admin/block/comment/{commentId}", commentId));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['댓글 차단']").value("댓글 차단 완료"));
            verify(commentService).blockComment(commentId);
        }
    }

    @Nested
    @DisplayName("unblockPost 메서드는")
    class Describe_unblockPost {
        @Test
        @DisplayName("게시글 차단을 해제한다")
        void unblocks_post() throws Exception {
            Long postId = 11L;
            ResultActions result = mockMvc.perform(get("/api/v1/admin/unblock/post/{postId}", postId));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['게시글 차단 해제']").value("게시글 차단 해제 완료"));
            verify(postService).unblockPost(postId);
        }
    }

    @Nested
    @DisplayName("unblockComment 메서드는")
    class Describe_unblockComment {
        @Test
        @DisplayName("댓글 차단을 해제한다")
        void unblocks_comment() throws Exception {
            Long commentId = 4L;
            ResultActions result = mockMvc.perform(get("/api/v1/admin/unblock/comment/{commentId}", commentId));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['댓글 차단 해제']").value("댓글 차단 해제 완료"));
            verify(commentService).unblockComment(commentId);
        }
    }

    @Nested
    @DisplayName("decideBadgeStatus 메서드는")
    class Describe_decideBadgeStatus {
        @Test
        @DisplayName("뱃지 요청 상태를 변경한다")
        void decides_badge_status() throws Exception {
            // given
            Long postId = 1L;
            when(postService.updateBadgeStatus(postId, true)).thenReturn(true);

            // when
            ResultActions result = mockMvc.perform(put("/api/v1/admin/badge/{postId}/{isBadgeAccepted}", postId, true));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.header.code").value(200));
            verify(postService).updateBadgeStatus(postId, true);
        }
    }

    @Nested
    @DisplayName("makeBadge 메서드는")
    class Describe_makeBadge {
        @Test
        @DisplayName("뱃지를 생성한다")
        void makes_badge() throws Exception {
            // given
            Long userId = 7L;
            BadgeCreateRequest request = new BadgeCreateRequest(1L, "배지");
            when(badgeService.createBadge(eq(userId), any(BadgeCreateRequest.class))).thenReturn(null);

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/admin/make-badge")
                    .with(authenticatedUser(userId))
                    .param("postId", "1")
                    .param("name", "배지"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.header.code").value(200));
            verify(badgeService).createBadge(eq(userId), any(BadgeCreateRequest.class));
        }
    }
}
