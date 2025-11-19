package com.example.campusin.api.message;

import com.example.campusin.application.message.MessageRoomService;
import com.example.campusin.common.config.security.SecurityConfig;
import com.example.campusin.domain.board.Board;
import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.message.Message;
import com.example.campusin.domain.message.MessageRoom;
import com.example.campusin.domain.message.dto.request.MessageRoomCreateRequest;
import com.example.campusin.domain.message.dto.response.MessageRoomIdResponse;
import com.example.campusin.domain.message.dto.response.MessageRoomListResponse;
import com.example.campusin.domain.message.dto.response.MessageRoomResponse;
import com.example.campusin.domain.oauth.ProviderType;
import com.example.campusin.domain.oauth.RoleType;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MessageRoomController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("MessageRoomController")
class MessageRoomControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MessageRoomService messageRoomService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("createMessageRoom 메서드는")
    class Describe_createMessageRoom {

        @Test
        @DisplayName("유효한 요청이면 쪽지방을 생성한다")
        void creates_message_room() throws Exception {
            // given
            Long userId = 1L;
            MessageRoomCreateRequest request = new MessageRoomCreateRequest(10L, 20L, "first message");
            given(messageRoomService.saveMessageRoom(eq(userId), any(MessageRoomCreateRequest.class), eq("IDEMP-KEY")))
                    .willReturn(new MessageRoomIdResponse(99L));

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/message-rooms")
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Idempotency-Key", "IDEMP-KEY")
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['쪽지방 생성이 완료되었습니다.']").value("MessageRoom create Successfully"));
            verify(messageRoomService).saveMessageRoom(eq(userId), any(MessageRoomCreateRequest.class), eq("IDEMP-KEY"));
        }

        @Test
        @DisplayName("요청 값이 비어 있으면 400을 반환한다")
        void fails_validation() throws Exception {
            // given
            String invalidBody = """
                    {
                      "createdFrom": null,
                      "receiverId": null,
                      "firstMessage": ""
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/message-rooms")
                    .with(authenticatedUser(1L))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Idempotency-Key", "KEY")
                    .content(invalidBody));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("C001"));
            verifyNoInteractions(messageRoomService);
        }
    }

    @Nested
    @DisplayName("getMessageRoom 메서드는")
    class Describe_getMessageRoom {

        @Test
        @DisplayName("쪽지방 상세를 반환한다")
        void returns_message_room() throws Exception {
            // given
            Long userId = 2L;
            Long roomId = 5L;
            MessageRoomResponse response = buildMessageRoomResponse();
            given(messageRoomService.getMessageRoom(eq(userId), any())).willReturn(response);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/message-rooms/{id}", roomId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['쪽지방 조회가 완료되었습니다.']").isMap());
            verify(messageRoomService).getMessageRoom(eq(userId), any());
        }
    }

    @Nested
    @DisplayName("getMessageRooms 메서드는")
    class Describe_getMessageRooms {

        @Test
        @DisplayName("쪽지방 리스트를 페이징하여 반환한다")
        void returns_message_rooms() throws Exception {
            // given
            Long userId = 3L;
            Page<MessageRoomListResponse> page = new PageImpl<>(
                    List.of(
                            MessageRoomListResponse.builder()
                                    .messageRoomId(1L)
                                    .interlocutorNickname("nick")
                                    .lastMessageContent("hi")
                                    .lastMessageSentTime(LocalDateTime.now())
                                    .build()
                    ),
                    PageRequest.of(0, 20),
                    1
            );
            given(messageRoomService.getMessageRooms(eq(userId), any())).willReturn(page);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/message-rooms")
                    .with(authenticatedUser(userId))
                    .param("page", "0")
                    .param("size", "20"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['쪽지방 리스트 조회가 완료되었습니다.'].content[0].messageRoomId").value(1L));
            verify(messageRoomService).getMessageRooms(eq(userId), any());
        }
    }

    @Nested
    @DisplayName("blockMessageRoom 메서드는")
    class Describe_blockMessageRoom {

        @Test
        @DisplayName("쪽지방을 차단한다")
        void blocks_message_room() throws Exception {
            // given
            Long userId = 4L;
            Long roomId = 7L;

            // when
            ResultActions result = mockMvc.perform(patch("/api/v1/message-rooms/{id}/block", roomId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['쪽지방 차단이 완료되었습니다.']").value("MESSAGE ROOM IS BLOCKED SUCCESSFULLY"));
            verify(messageRoomService).blockMessageRoom(userId, roomId);
        }
    }

    @Nested
    @DisplayName("deleteMessageRoom 메서드는")
    class Describe_deleteMessageRoom {

        @Test
        @DisplayName("쪽지방을 삭제한다")
        void deletes_message_room() throws Exception {
            // given
            Long userId = 5L;
            Long roomId = 9L;

            // when
            ResultActions result = mockMvc.perform(patch("/api/v1/message-rooms/{id}/delete", roomId)
                    .with(authenticatedUser(userId)));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['쪽지방 삭제가 완료되었습니다.']").value("DELETE MESSAGE SUCCESSFULLY"));
            verify(messageRoomService).deleteMessageRoom(userId, roomId);
        }
    }

    private MessageRoomResponse buildMessageRoomResponse() {
        User sender = User.builder()
                .loginId("sender")
                .username("sender")
                .profileImageUrl("")
                .providerType(ProviderType.LOCAL)
                .roleType(RoleType.USER)
                .createdAt(null)
                .modifiedAt(null)
                .nickname("sender")
                .build();

        User receiver = User.builder()
                .loginId("receiver")
                .username("receiver")
                .profileImageUrl("")
                .providerType(ProviderType.LOCAL)
                .roleType(RoleType.USER)
                .createdAt(null)
                .modifiedAt(null)
                .nickname("receiver")
                .build();

        Board board = Board.builder()
                .boardType(BoardType.Free)
                .build();

        Post post = Post.builder()
                .title("title")
                .content("content")
                .user(sender)
                .board(board)
                .price(0L)
                .studyGroupId(1L)
                .tag(null)
                .build();

        MessageRoom messageRoom = MessageRoom.builder()
                .initialSender(sender)
                .initialReceiver(receiver)
                .createdFrom(post)
                .build();

        Page<Message> messages = Page.empty();

        return MessageRoomResponse.builder()
                .messageRoom(messageRoom)
                .messages(messages)
                .interlocutor(receiver)
                .build();
    }

}
