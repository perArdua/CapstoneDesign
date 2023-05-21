package com.example.campusin.api.todo;

import com.example.campusin.application.todo.TodoService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.domain.todo.dto.request.TodoRequest;
import com.example.campusin.domain.todo.dto.request.TodoUpdateRequest;
import com.example.campusin.domain.todo.dto.response.TodoIdResponse;
import com.example.campusin.domain.todo.dto.response.TodoResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"TODO API"})
@RestController
@RequestMapping("/api/v1/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = TodoIdResponse.class)
            }
    )
    @Operation(summary = "Todo 생성")
    @PostMapping
    public ApiResponse create(@AuthenticationPrincipal UserPrincipal principal,
                              @RequestBody @Validated TodoRequest request) {

        return ApiResponse.success("Todo 생성이 완료되었습니다.", todoService.createTodo(principal.getUserId(), request));
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = TodoIdResponse.class)
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

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = TodoResponse.class, responseContainer = "Page")
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