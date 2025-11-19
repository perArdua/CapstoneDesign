package com.example.campusin.common.exception.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 서비스 전역에서 사용하는 에러 코드와 메시지를 정의한다.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C000", "서버에서 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 요청 값입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C002", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C003", "접근 권한이 없습니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "대상을 찾을 수 없습니다."),

    // Auth
    OAUTH_PROVIDER_MISMATCH(HttpStatus.BAD_REQUEST, "A001", "OAuth provider가 일치하지 않습니다."),
    TOKEN_VALIDATION_FAILED(HttpStatus.UNAUTHORIZED, "A002", "토큰 검증에 실패했습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),

    // Post & Comment
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P002", "댓글을 찾을 수 없습니다."),
    COMMENT_REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "P003", "댓글 신고를 찾을 수 없습니다."),
    COMMENT_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "P004", "댓글 좋아요를 찾을 수 없습니다."),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "P005", "게시판을 찾을 수 없습니다."),
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "P006", "태그를 찾을 수 없습니다."),

    // Study Group
    STUDY_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "스터디그룹을 찾을 수 없습니다."),
    STUDY_GROUP_FULL(HttpStatus.BAD_REQUEST, "S002", "스터디그룹 인원이 가득 찼습니다."),
    STUDY_GROUP_ALREADY_MEMBER(HttpStatus.BAD_REQUEST, "S003", "이미 스터디그룹에 속해 있습니다."),
    STUDY_GROUP_NOT_MEMBER(HttpStatus.BAD_REQUEST, "S004", "스터디그룹 멤버가 아닙니다."),
    STUDY_GROUP_MEMBER_REMOVE_FAILED(HttpStatus.BAD_REQUEST, "S005", "스터디그룹 멤버 삭제에 실패했습니다."),

    // Message
    MESSAGE_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "메시지 방을 찾을 수 없습니다."),
    MESSAGE_SEND_NOT_ALLOWED(HttpStatus.FORBIDDEN, "M002", "메시지를 보낼 권한이 없습니다."),
    MESSAGE_READ_NOT_ALLOWED(HttpStatus.FORBIDDEN, "M003", "메시지를 읽을 권한이 없습니다."),
    MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "M004", "메시지를 전송할 수 없습니다."),

    // Timer
    TIMER_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "타이머를 찾을 수 없습니다."),
    TIMER_USER_MISMATCH(HttpStatus.FORBIDDEN, "T002", "타이머 소유자가 아닙니다."),

    // Todo
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "TD01", "할 일을 찾을 수 없습니다."),

    // Badge
    BADGE_UNAUTHORIZED(HttpStatus.FORBIDDEN, "B001", "배지 발급 권한이 없습니다."),
    BADGE_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "B002", "배지 대상 유저를 찾을 수 없습니다."),
    BADGE_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "B003", "배지 대상 게시글을 찾을 수 없습니다."),

    // Statistics
    STATISTICS_NOT_FOUND(HttpStatus.NOT_FOUND, "ST01", "통계 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
