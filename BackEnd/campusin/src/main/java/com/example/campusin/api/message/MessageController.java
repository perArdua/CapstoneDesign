package com.example.campusin.api.message;

import com.example.campusin.application.message.MessageService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.message.dto.request.MessageSendRequest;
import com.example.campusin.domain.message.dto.response.MessageResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"쪽지 API"})
@RestController
@RequestMapping("/api/v1/message-rooms")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "쪽지 전송 성공"),
            }
    )
    @Operation(summary = "쪽지 전송")
    @PostMapping("/{messageRoomId}/messages")
    public ApiResponse sendMessage(@AuthenticationPrincipal UserPrincipal principal,
                                   @PathVariable("messageRoomId") Long messageRoomId,
                                   @Valid @RequestBody final MessageSendRequest request) {

        messageService.sendMessage(principal.getUserId(), messageRoomId, request);
        return ApiResponse.success("쪽지 전송 완료", "Message send successfully");
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "쪽지 재전송 완료"),
            }
    )
    @Operation(summary = "쪽지방 생성에서 리디렉트된 쪽지를 전송")
    @PostMapping("/{messageRoomId}/redirect-message")
    public ApiResponse sendRedirectedMessage(@AuthenticationPrincipal UserPrincipal principal,
                                             @PathVariable("messageRoomId") Long messageRoomId,
                                             @RequestBody String message) {

        MessageSendRequest request = new MessageSendRequest(message);
        messageService.sendMessage(principal.getUserId(), messageRoomId, request);

        return ApiResponse.success("쪽지 재전송 완료", "Redirect Message send successfully");
    }

    @ApiResponses(
            value = {
                    @io.swagger.annotations.ApiResponse(code = 200, message = "쪽지 리스트 조회 성공", response = MessageResponse.class, responseContainer = "Page"),
            }
    )
    @Operation(summary = "쪽지 리스트 조회")
    @GetMapping("/{messageRoomId}/messages")
    public ApiResponse getAllMessages(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("messageRoomId") Long messageRoomId,
                                                      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable) {
        return ApiResponse.success("쪽지 리스트 조회 완료", messageService.getAllMessages(principal.getUserId(), messageRoomId, pageable));

    }
}