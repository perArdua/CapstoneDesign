package com.example.campusin.api.message;

import com.example.campusin.application.message.MessageRoomService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.message.dto.request.MessageRoomCreateRequest;
import com.example.campusin.domain.message.dto.request.MessageRoomGetRequest;
import com.example.campusin.domain.message.dto.response.MessageRoomIdResponse;
import com.example.campusin.domain.message.dto.response.MessageRoomListResponse;
import com.example.campusin.domain.message.dto.response.MessageRoomResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/message-rooms")
@RequiredArgsConstructor
public class MessageRoomController {

    private final MessageRoomService messageRoomService;

    @PostMapping
    public ApiResponse createMessageRoom(@AuthenticationPrincipal UserPrincipal principal,
                                         @Valid @RequestBody final MessageRoomCreateRequest request,
                                         RedirectAttributes redirectAttributes) throws URISyntaxException {

        Optional<Long> maybeMessageRoomId = messageRoomService.getMessageRoomId(principal.getUserId(), request.getCreatedFrom(), request.getReceiverId());


        if (maybeMessageRoomId.isPresent()) {
            URI redirectUri = new URI(
                    new StringBuilder().append("/api/v1/message-rooms/").append(maybeMessageRoomId.get())
                            .append("/redirect-message?userId=").append(principal.getUserId()).toString()
            );


            redirectAttributes.addFlashAttribute("message", request.getFirstMessage());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUri);

            return ApiResponse.success("이미 존재하는 쪽지방입니다.", "쪽지 전송 요청으로 리디렉트 되었습니다.");
        }

        MessageRoomIdResponse response = messageRoomService.saveMessageRoom(principal.getUserId(), request);

        URI redirectUri = new URI(
                new StringBuilder().append("/api/v1/message-rooms/").append(response.getMessageRoomId())
                        .append("?userId=").append(principal.getUserId()).toString());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);

        return ApiResponse.success("쪽지방 생성이 완료되었습니다.", "MessageRoom create Successfully");
    }

    @GetMapping("/{messageRoomId}")
    public ApiResponse getMessageRoom(@AuthenticationPrincipal UserPrincipal principal,
                                      @PathVariable("messageRoomId") Long messageRoomId) {

        MessageRoomGetRequest request = new MessageRoomGetRequest(messageRoomId);
        MessageRoomResponse response = messageRoomService.getMessageRoom(principal.getUserId(), request);

        return ApiResponse.success("쪽지방 조회가 완료되었습니다.", response);
    }

    //쪽지방 리스트 조회

    @GetMapping
    public ApiResponse getMessageRooms(@AuthenticationPrincipal UserPrincipal principal,
                                       @PageableDefault(size = 20, sort = "MODIFIED_AT", direction = Sort.Direction.DESC) final Pageable pageable) {

        Page<MessageRoomListResponse> response = messageRoomService.getMessageRooms(principal.getUserId(), pageable);
        return ApiResponse.success("쪽지방 리스트 조회가 완료되었습니다.", response);
    }

    @PatchMapping("/{messageRoomId}/block")
    public ApiResponse blockMessageRoom(@AuthenticationPrincipal UserPrincipal principal,
                                        @PathVariable("messageRoomId") Long messageRoomId) {

        messageRoomService.blockMessageRoom(principal.getUserId(), messageRoomId);

        return ApiResponse.success("쪽지방 차단이 완료되었습니다.", "MESSAGE ROOM IS BLOCKED SUCCESSFULLY");
    }

    @PatchMapping("/{messageRoomId}/delete")
    public ApiResponse deleteMessageRoom(@AuthenticationPrincipal UserPrincipal principal,
                                         @PathVariable("messageRoomId") Long messageRoomId) {

        messageRoomService.deleteMessageRoom(principal.getUserId(), messageRoomId);
        return ApiResponse.success("쪽지방 삭제가 완료되었습니다.", "DELETE MESSAGE SUCCESSFULLY");
    }
}
