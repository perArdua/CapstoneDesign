package com.example.campusin.application.todo;

import com.example.campusin.application.todo.exception.TodoNotFoundException;
import com.example.campusin.application.user.exception.UserNotFoundException;
import com.example.campusin.domain.todo.Todo;
import com.example.campusin.domain.todo.dto.request.TodoRequest;
import com.example.campusin.domain.todo.dto.request.TodoUpdateRequest;
import com.example.campusin.domain.todo.dto.response.TodoIdResponse;
import com.example.campusin.domain.todo.dto.response.TodoResponse;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.todo.TodoRepository;
import com.example.campusin.infra.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TodoService")
class TodoServiceTest {

    @Mock
    TodoRepository todoRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    TodoService todoService;

    @Nested
    @DisplayName("createTodo 메서드는")
    class Describe_createTodo {

        @Nested
        @DisplayName("사용자가 존재하면")
        class Context_when_user_exists {

            @Test
            @DisplayName("요청 값으로 Todo를 두 번 저장하고 ID를 반환한다")
            void Todo를_저장한다() {
                // given
                Long userId = 1L;
                User user = new User();
                user.setId(userId);
                TodoRequest request = new TodoRequest();
                ReflectionTestUtils.setField(request, "title", "자료구조");
                ReflectionTestUtils.setField(request, "completed", false);

                ArgumentCaptor<Todo> todoCaptor = ArgumentCaptor.forClass(Todo.class);
                when(userRepository.findById(userId)).thenReturn(Optional.of(user));
                doAnswer(invocation -> {
                    Todo todo = invocation.getArgument(0);
                    if (todo.getId() == null) {
                        todo.setId(7L);
                    }
                    return todo;
                }).when(todoRepository).save(todoCaptor.capture());

                // when
                TodoIdResponse response = todoService.createTodo(userId, request);

                // then
                assertThat(response.getId()).isEqualTo(7L);
                List<Todo> savedTodos = todoCaptor.getAllValues();
                assertThat(savedTodos).hasSize(2);
                Todo firstSaved = savedTodos.get(0);
                assertThat(firstSaved.getUser()).isEqualTo(user);
                assertThat(firstSaved.getTitle()).isEqualTo("자료구조");
                assertThat(firstSaved.getCompleted()).isFalse();
                verify(todoRepository, times(2)).save(any(Todo.class));
            }
        }

        @Nested
        @DisplayName("사용자가 없으면")
        class Context_when_user_missing {

            @Test
            @DisplayName("UserNotFoundException을 던진다")
            void 예외를_던진다() {
                // given
                Long userId = 9L;
                TodoRequest request = new TodoRequest();
                ReflectionTestUtils.setField(request, "title", "OS");
                ReflectionTestUtils.setField(request, "completed", true);

                when(userRepository.findById(userId)).thenReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> todoService.createTodo(userId, request))
                        .isInstanceOf(UserNotFoundException.class);

                verifyNoInteractions(todoRepository);
            }
        }
    }

    @Nested
    @DisplayName("getAllTodoList 메서드는")
    class Describe_getAllTodoList {

        @Nested
        @DisplayName("사용자가 존재하면")
        class Context_when_user_exists {

            @Test
            @DisplayName("TodoResponse 페이지를 반환한다")
            void 목록을_반환한다() {
                // given
                Long userId = 2L;
                User user = new User();
                user.setId(userId);
                Todo todo = Todo.builder()
                        .user(user)
                        .title("알고리즘")
                        .completed(false)
                        .build();
                todo.setId(10L);
                PageRequest pageable = PageRequest.of(0, 2);

                when(userRepository.findById(userId)).thenReturn(Optional.of(user));
                when(todoRepository.findAllMyTodoList(userId, pageable))
                        .thenReturn(new PageImpl<>(List.of(todo), pageable, 1));

                // when
                Page<TodoResponse> responses = todoService.getAllTodoList(userId, pageable);

                // then
                assertThat(responses.getTotalElements()).isEqualTo(1);
                TodoResponse response = responses.getContent().get(0);
                assertThat(response.getUserId()).isEqualTo(userId);
                assertThat(response.getTodoId()).isEqualTo(10L);
                assertThat(response.getTitle()).isEqualTo("알고리즘");
                assertThat(response.getCompleted()).isFalse();
                verify(todoRepository).findAllMyTodoList(userId, pageable);
            }
        }

        @Nested
        @DisplayName("사용자가 없으면")
        class Context_when_user_missing {

            @Test
            @DisplayName("UserNotFoundException을 던진다")
            void 예외를_던진다() {
                // given
                PageRequest pageable = PageRequest.of(1, 5);
                when(userRepository.findById(3L)).thenReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> todoService.getAllTodoList(3L, pageable))
                        .isInstanceOf(UserNotFoundException.class);

                verifyNoInteractions(todoRepository);
            }
        }
    }

    @Nested
    @DisplayName("updateTodo 메서드는")
    class Describe_updateTodo {

        @Nested
        @DisplayName("Todo가 존재하면")
        class Context_when_todo_exists {

            @Test
            @DisplayName("제목과 완료 여부를 수정하고 ID를 반환한다")
            void 수정한다() {
                // given
                Long todoId = 5L;
                User user = new User();
                user.setId(1L);
                Todo todo = Todo.builder()
                        .user(user)
                        .title("기존 제목")
                        .completed(true)
                        .build();
                todo.setId(todoId);

                TodoUpdateRequest request = new TodoUpdateRequest();
                ReflectionTestUtils.setField(request, "title", "새 제목");
                ReflectionTestUtils.setField(request, "completed", false);

                when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
                when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

                // when
                TodoIdResponse response = todoService.updateTodo(todoId, request);

                // then
                assertThat(response.getId()).isEqualTo(todoId);
                assertThat(todo.getTitle()).isEqualTo("새 제목");
                assertThat(todo.getCompleted()).isFalse();
                verify(todoRepository).save(todo);
            }
        }

        @Nested
        @DisplayName("Todo가 없으면")
        class Context_when_todo_missing {

            @Test
            @DisplayName("TodoNotFoundException을 던진다")
            void 예외를_던진다() {
                // given
                when(todoRepository.findById(99L)).thenReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> todoService.updateTodo(99L, new TodoUpdateRequest()))
                        .isInstanceOf(TodoNotFoundException.class);

                verify(todoRepository, never()).save(any());
            }
        }
    }

    @Nested
    @DisplayName("deleteTodo 메서드는")
    class Describe_deleteTodo {

        @Nested
        @DisplayName("사용자와 Todo가 존재하면")
        class Context_when_both_exist {

            @Test
            @DisplayName("Todo를 삭제한다")
            void 삭제한다() {
                // given
                Long userId = 4L;
                Long todoId = 6L;
                User user = new User();
                user.setId(userId);
                Todo todo = Todo.builder().user(user).title("OS").completed(false).build();
                todo.setId(todoId);

                when(userRepository.findById(userId)).thenReturn(Optional.of(user));
                when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

                // when
                todoService.deleteTodo(userId, todoId);

                // then
                verify(todoRepository).deleteById(todoId);
            }
        }

        @Nested
        @DisplayName("사용자가 없으면")
        class Context_when_user_missing {

            @Test
            @DisplayName("UserNotFoundException을 던진다")
            void 예외를_던진다() {
                // given
                when(userRepository.findById(10L)).thenReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> todoService.deleteTodo(10L, 1L))
                        .isInstanceOf(UserNotFoundException.class);

                verifyNoInteractions(todoRepository);
            }
        }

        @Nested
        @DisplayName("Todo가 없으면")
        class Context_when_todo_missing {

            @Test
            @DisplayName("TodoNotFoundException을 던진다")
            void 예외를_던진다() {
                // given
                User user = new User();
                when(userRepository.findById(2L)).thenReturn(Optional.of(user));
                when(todoRepository.findById(3L)).thenReturn(Optional.empty());

                // when // then
                assertThatThrownBy(() -> todoService.deleteTodo(2L, 3L))
                        .isInstanceOf(TodoNotFoundException.class);

                verify(todoRepository, never()).deleteById(any());
            }
        }
    }
}
