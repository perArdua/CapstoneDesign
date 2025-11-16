package com.example.campusin.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 서비스 전역에서 사용하는 에러 코드와 메시지를 정의한다.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C000", "서버에서 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 요청 값입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C002", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C003", "접근 권한이 없습니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "대상을 찾을 수 없습니다."),

    OAUTH_PROVIDER_MISMATCH(HttpStatus.BAD_REQUEST, "A001", "OAuth provider가 일치하지 않습니다."),
    TOKEN_VALIDATION_FAILED(HttpStatus.UNAUTHORIZED, "A002", "토큰 검증에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
