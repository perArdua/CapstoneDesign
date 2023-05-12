package com.example.campusin.api.message;

import com.example.campusin.application.message.MessageService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.message.dto.request.MessageSendRequest;
import com.example.campusin.domain.message.dto.response.MessageResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/message-rooms")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/{messageRoomId}/messages")
    public ApiResponse sendMessage(@AuthenticationPrincipal UserPrincipal principal,
                                   @PathVariable("messageRoomId") Long messageRoomId,
                                   @Valid @RequestBody final MessageSendRequest request) {

        messageService.sendMessage(principal.getUserSeq(), messageRoomId, request);
        return ApiResponse.success("쪽지 전송 완료", "Message send successfully");
    }

    @PostMapping("/{messageRoomId}/redirect-message")
    public ApiResponse sendRedirectedMessage(@AuthenticationPrincipal UserPrincipal principal,
                                             @PathVariable("messageRoomId") Long messageRoomId,
                                             @RequestBody String message) {

        MessageSendRequest request = new MessageSendRequest(message);
        messageService.sendMessage(principal.getUserSeq(), messageRoomId, request);

        return ApiResponse.success("쪽지 재전송 완료", "Redirect Message send successfully");
    }

    @GetMapping("/{messageRoomId}/messages")
    public ApiResponse getAllMessages(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("messageRoomId") Long messageRoomId,
                                                      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable) {

        Page<MessageResponse> allMessages = messageService.getAllMessages(principal.getUserSeq(), messageRoomId, pageable);

        return ApiResponse.success("쪽지 리스트 조회 완료", messageService.getAllMessages(principal.getUserSeq(), messageRoomId, pageable));

    }
}