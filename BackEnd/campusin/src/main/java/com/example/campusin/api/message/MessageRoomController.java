package com.example.campusin.api.message;

import com.example.campusin.application.message.MessageRoomService;
import com.example.campusin.common.response.ApiResponse;
import com.example.campusin.domain.message.dto.request.MessageRoomCreateRequest;
import com.example.campusin.domain.message.dto.request.MessageRoomGetRequest;
import com.example.campusin.domain.message.dto.response.MessageRoomIdResponse;
import com.example.campusin.domain.message.dto.response.MessageRoomListResponse;
import com.example.campusin.domain.message.dto.response.MessageRoomResponse;
import com.example.campusin.domain.oauth.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
@io.swagger.v3.oas.annotations.tags.Tag(name = "쪽지방 API")
@RestController
@RequestMapping("/api/v1/message-rooms")
@RequiredArgsConstructor
public class MessageRoomController {

    private final MessageRoomService messageRoomService;

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "쪽지방 생성 성공", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = MessageRoomIdResponse.class))),
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "쪽지방 생성하기", description = "쪽지방을 생성하는 요청 입니다. MessageRoom ID 반환")
    @PostMapping
    public ApiResponse createMessageRoom(@AuthenticationPrincipal UserPrincipal principal,
                                         @Valid @RequestBody final MessageRoomCreateRequest request,
                                         @RequestHeader("Idempotency-Key") String idempotencyKey,
                                         RedirectAttributes redirectAttributes) throws URISyntaxException {

        MessageRoomIdResponse response = messageRoomService.saveMessageRoom(
                principal.getUserId(), request, idempotencyKey
        );

        URI redirectUri = new URI(
                new StringBuilder()
                        .append("/api/v1/message-rooms/")
                        .append(response.getMessageRoomId())
                        .append("?userId=").append(principal.getUserId())
                        .toString()
        );

        redirectAttributes.addFlashAttribute("message", request.getFirstMessage());

        return ApiResponse.success("쪽지방 생성이 완료되었습니다.", "MessageRoom create Successfully");
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "쪽지방 정보와 최근 쪽지 조회 성공", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = MessageRoomResponse.class))),
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "쪽지방 정보와 최근 쪽지 조회하기")
    @GetMapping("/{messageRoomId}")
    public ApiResponse getMessageRoom(@AuthenticationPrincipal UserPrincipal principal,
                                      @PathVariable("messageRoomId") Long messageRoomId) {

        MessageRoomGetRequest request = new MessageRoomGetRequest(messageRoomId);
        MessageRoomResponse response = messageRoomService.getMessageRoom(principal.getUserId(), request);

        return ApiResponse.success("쪽지방 조회가 완료되었습니다.", response);
    }

    //쪽지방 리스트 조회
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "쪽지방 리스트 조회 성공", content = @io.swagger.v3.oas.annotations.media.Content(array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = MessageRoomListResponse.class)))),
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "쪽지방 리스트 조회하기")
    @GetMapping
    public ApiResponse getMessageRooms(@AuthenticationPrincipal UserPrincipal principal,
                                       @PageableDefault(size = 20, sort = "MODIFIED_AT", direction = Sort.Direction.DESC) final Pageable pageable) {

        Page<MessageRoomListResponse> response = messageRoomService.getMessageRooms(principal.getUserId(), pageable);
        return ApiResponse.success("쪽지방 리스트 조회가 완료되었습니다.", response);
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "쪽지방 차단 성공"),
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "쪽지방 차단하기")
    @PatchMapping("/{messageRoomId}/block")
    public ApiResponse blockMessageRoom(@AuthenticationPrincipal UserPrincipal principal,
                                        @PathVariable("messageRoomId") Long messageRoomId) {

        messageRoomService.blockMessageRoom(principal.getUserId(), messageRoomId);

        return ApiResponse.success("쪽지방 차단이 완료되었습니다.", "MESSAGE ROOM IS BLOCKED SUCCESSFULLY");
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "쪽지방 삭제 성공")
            }
    )
    @io.swagger.v3.oas.annotations.Operation(summary = "쪽지방 삭제하기")
    @PatchMapping("/{messageRoomId}/delete")
    public ApiResponse deleteMessageRoom(@AuthenticationPrincipal UserPrincipal principal,
                                         @PathVariable("messageRoomId") Long messageRoomId) {

        messageRoomService.deleteMessageRoom(principal.getUserId(), messageRoomId);
        return ApiResponse.success("쪽지방 삭제가 완료되었습니다.", "DELETE MESSAGE SUCCESSFULLY");
    }
}
