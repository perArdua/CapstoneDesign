package com.example.campusin.api.message;

import com.example.campusin.application.message.MessageService;
import com.example.campusin.common.config.security.SecurityConfig;
import com.example.campusin.domain.message.dto.request.MessageSendRequest;
import com.example.campusin.domain.message.dto.response.MessageResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MessageController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("MessageController")
class MessageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MessageService messageService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("sendMessage 메서드는")
    class Describe_sendMessage {

        @Test
        @DisplayName("유효한 요청이면 200과 성공 메시지를 반환한다")
        void sends_message() throws Exception {
            // given
            Long userId = 1L;
            Long roomId = 3L;
            MessageSendRequest request = new MessageSendRequest("내용");

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/message-rooms/{roomId}/messages", roomId)
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['쪽지 전송 완료']").value("Message send successfully"));
            verify(messageService).sendMessage(eq(userId), eq(roomId), any(MessageSendRequest.class));
        }

        @Test
        @DisplayName("메시지가 비어 있으면 400을 반환한다")
        void blank_message() throws Exception {
            // given
            String body = """
                    {
                      "message": ""
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/message-rooms/{roomId}/messages", 1L)
                    .with(authenticatedUser(1L))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("C001"))
                    .andExpect(jsonPath("$.errors[0].field").value("message"));
            verifyNoInteractions(messageService);
        }
    }

    @Nested
    @DisplayName("sendRedirectedMessage 메서드는")
    class Describe_sendRedirectedMessage {

        @Test
        @DisplayName("문자열 본문을 받아 메시지를 재전송한다")
        void sends_redirect_message() throws Exception {
            // given
            Long userId = 2L;
            Long roomId = 5L;
            String body = "\"재전송 내용\"";

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/message-rooms/{roomId}/redirect-message", roomId)
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['쪽지 재전송 완료']").value("Redirect Message send successfully"));
            verify(messageService).sendMessage(eq(userId), eq(roomId), any(MessageSendRequest.class));
        }
    }

    @Nested
    @DisplayName("getAllMessages 메서드는")
    class Describe_getAllMessages {

        @Test
        @DisplayName("쪽지 목록을 반환한다")
        void returns_messages() throws Exception {
            // given
            Long userId = 4L;
            Long roomId = 9L;
            PageRequest pageable = PageRequest.of(0, 2);
            Page<MessageResponse> page = Page.empty(pageable);
            given(messageService.getAllMessages(eq(userId), eq(roomId), any(Pageable.class)))
                    .willReturn(page);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/message-rooms/{roomId}/messages", roomId)
                    .with(authenticatedUser(userId))
                    .param("page", "0")
                    .param("size", "2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['쪽지 리스트 조회 완료'].content").isEmpty());
            verify(messageService).getAllMessages(eq(userId), eq(roomId), any(Pageable.class));
        }
    }
}
