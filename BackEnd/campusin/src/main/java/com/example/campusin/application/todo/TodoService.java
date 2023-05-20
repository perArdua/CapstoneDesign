package com.example.campusin.application.todo;

import com.example.campusin.domain.todo.Todo;
import com.example.campusin.domain.todo.dto.request.TodoRequest;
import com.example.campusin.domain.todo.dto.request.TodoUpdateRequest;
import com.example.campusin.domain.todo.dto.response.TodoIdResponse;
import com.example.campusin.domain.todo.dto.response.TodoResponse;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.todo.TodoRepository;
import com.example.campusin.infra.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    // todolist 생성
    @Transactional
    public TodoIdResponse createTodo(Long userId, TodoRequest request){
        User user = findUser(userId);
        Todo todo = todoRepository.save(Todo.builder()
                    .user(user)
                    .title(request.getTitle())
                    .completed(request.getCompleted())
                    .build());
        return new TodoIdResponse(todoRepository.save(todo).getId());
    }

    // todolist 전체 목록 조회
    @Transactional(readOnly = true)
    public Page<TodoResponse> getAllTodoList(Long userId, Pageable pageable) {
        findUser(userId);
        return todoRepository.findAll(pageable).map(TodoResponse::new);
    }

    // todolist 수정
    @Transactional
    public TodoIdResponse updateTodo(Long todoId, TodoUpdateRequest request) {
        Todo todo = findTodo(todoId);
        todo.updateTodo(request);
        return new TodoIdResponse(todoRepository.save(todo).getId());

    }

    @Transactional
    public void deleteTodo(Long userId, Long todoId){

        findUser(userId);
        findTodo(todoId);
        todoRepository.deleteById(todoId);
    }

    private Todo findTodo(Long todoId) {
        return todoRepository.findById(todoId).orElseThrow(() -> new IllegalArgumentException("TODO NOT FOUND"));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("USER NOT FOUND")
        );
    }
}
