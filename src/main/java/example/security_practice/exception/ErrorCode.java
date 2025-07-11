package example.security_practice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 커스텀 에러 코드를 정의합니다.
 * 에러 이름(HTTP 응답 코드, 커스텀 코드, 커스텀 메시지)
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    HAS_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "재확인 비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 Member를 찾을 수 없습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),

    INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "요청은 application/json 타입이어야 합니다."),
    TOKEN_NULL(HttpStatus.BAD_REQUEST, "요청에 토큰이 존재하지 않습니다."),
    TOKEN_NOT_BEARER(HttpStatus.BAD_REQUEST, "토큰은 Bearer로 시작해야 합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    JWT_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "JWT 인증에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
