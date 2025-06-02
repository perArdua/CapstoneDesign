package com.example.campusin.api.todo;

import com.example.campusin.application.todo.TodoService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.todo.dto.request.TodoRequest;
import com.example.campusin.domain.todo.dto.request.TodoUpdateRequest;
import com.example.campusin.domain.todo.dto.response.TodoIdResponse;
import com.example.campusin.domain.todo.dto.response.TodoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "TODO API")
@RestController
@RequestMapping("/api/v1/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TodoIdResponse.class)))
            }
    )
    @Operation(summary = "Todo 생성")
    @PostMapping
    public ApiResponse create(@AuthenticationPrincipal UserPrincipal principal,
                              @RequestBody @Validated TodoRequest request) {

        return ApiResponse.success("Todo 생성이 완료되었습니다.", todoService.createTodo(principal.getUserId(), request));
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TodoIdResponse.class)))
            }
    )
    @Operation(summary = "Todo 수정")
    @PatchMapping("/{todoId}")
    public ApiResponse update(@PathVariable(name = "todoId") Long todoId,
                              @RequestBody @Validated TodoUpdateRequest request){
        return ApiResponse.success("Todo 수정이 완료되었습니다.", todoService.updateTodo(todoId, request));
    }

    @Operation(summary = "Todo 삭제")
    @PatchMapping("/{todoId}/delete")
    public ApiResponse delete(@AuthenticationPrincipal UserPrincipal principal,
                              @PathVariable("todoId") Long todoId) {
        todoService.deleteTodo(principal.getUserId(),todoId);

        return ApiResponse.success("Todo 삭제가 완료되었습니다.", "DELETE TODO SUCCESSFULLY");
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TodoResponse.class))))
            }
    )
    @Operation(summary = "Todo list 조회")
    @GetMapping
    public ApiResponse getTodoList(@AuthenticationPrincipal UserPrincipal principal,
                                   @PageableDefault(
                                           sort = {"createdAt"},
                                           direction = Sort.Direction.DESC
                                   ) final Pageable pageable) {
        Page<TodoResponse> response = todoService.getAllTodoList(principal.getUserId(), pageable);
        return ApiResponse.success("Todo 조회가 완료되었습니다.", response);

    }

}