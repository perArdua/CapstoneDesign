package com.example.campusin.api.todo;

import com.example.campusin.application.todo.TodoService;
import com.example.campusin.application.todo.exception.TodoNotFoundException;
import com.example.campusin.common.config.security.SecurityConfig;
import com.example.campusin.domain.todo.dto.request.TodoRequest;
import com.example.campusin.domain.todo.dto.request.TodoUpdateRequest;
import com.example.campusin.domain.todo.dto.response.TodoIdResponse;
import com.example.campusin.domain.todo.dto.response.TodoResponse;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TodoController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("TodoController")
class TodoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TodoService todoService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("create 메서드는")
    class Describe_create {

        @Test
        @DisplayName("Todo를 생성하고 ID를 반환한다")
        void creates_todo() throws Exception {
            // given
            Long userId = 1L;
            TodoRequest request = new TodoRequest();
            request.setTitle("할 일");
            request.setCompleted(false);
            given(todoService.createTodo(eq(userId), any(TodoRequest.class)))
                    .willReturn(new TodoIdResponse(10L));

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/todo")
                    .with(authenticatedUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['Todo 생성이 완료되었습니다.'].id").value(10L));
            verify(todoService).createTodo(eq(userId), any(TodoRequest.class));
        }

        @Test
        @DisplayName("제목이 비어 있으면 400을 반환한다")
        void blank_title() throws Exception {
            // given
            String body = """
                    {
                      "title": "",
                      "completed": false
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/todo")
                    .with(authenticatedUser(1L))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("C001"))
                    .andExpect(jsonPath("$.errors[0].field").value("title"));
            verifyNoInteractions(todoService);
        }
    }

    @Nested
    @DisplayName("update 메서드는")
    class Describe_update {

        @Test
        @DisplayName("Todo를 수정한다")
        void updates_todo() throws Exception {
            // given
            Long todoId = 5L;
            String body = """
                    {
                      "title": "수정",
                      "completed": true
                    }
                    """;
            given(todoService.updateTodo(eq(todoId), any(TodoUpdateRequest.class)))
                    .willReturn(new TodoIdResponse(todoId));

            // when
            ResultActions result = mockMvc.perform(patch("/api/v1/todo/{todoId}", todoId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['Todo 수정이 완료되었습니다.'].id").value(5L));
            verify(todoService).updateTodo(eq(todoId), any(TodoUpdateRequest.class));
        }

        @Test
        @DisplayName("Todo가 없으면 404를 반환한다")
        void todo_not_found() throws Exception {
            // given
            Long todoId = 5L;
            String body = """
                    {
                      "title": "수정",
                      "completed": false
                    }
                    """;
            given(todoService.updateTodo(eq(todoId), any(TodoUpdateRequest.class)))
                    .willThrow(new TodoNotFoundException());

            // when
            ResultActions result = mockMvc.perform(patch("/api/v1/todo/{todoId}", todoId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("TD01"));
            verify(todoService).updateTodo(eq(todoId), any(TodoUpdateRequest.class));
        }
    }

    @Nested
    @DisplayName("delete 메서드는")
    class Describe_delete {

        @Test
        @DisplayName("Todo를 삭제한다")
        void deletes_todo() throws Exception {
            // given
            Long userId = 2L;
            Long todoId = 7L;

            // when
            ResultActions result = mockMvc.perform(patch("/api/v1/todo/{todoId}/delete", todoId)
                    .with(authenticatedUser(userId)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['Todo 삭제가 완료되었습니다.']").value("DELETE TODO SUCCESSFULLY"));
            verify(todoService).deleteTodo(userId, todoId);
        }
    }

    @Nested
    @DisplayName("getTodoList 메서드는")
    class Describe_getTodoList {

        @Test
        @DisplayName("Todo 목록을 반환한다")
        void returns_todo_list() throws Exception {
            // given
            Long userId = 3L;
            PageRequest pageable = PageRequest.of(0, 2);
            TodoResponse response = TodoResponse.builder()
                    .userId(userId)
                    .todoId(1L)
                    .title("할 일")
                    .completed(false)
                    .build();
            Page<TodoResponse> page = new PageImpl<>(List.of(response), pageable, 1);
            given(todoService.getAllTodoList(eq(userId), any(Pageable.class)))
                    .willReturn(page);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/todo")
                    .with(authenticatedUser(userId))
                    .param("page", "0")
                    .param("size", "2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.body['Todo 조회가 완료되었습니다.'].content[0].todoId").value(1L));
            verify(todoService).getAllTodoList(eq(userId), any(Pageable.class));
        }
    }

}
