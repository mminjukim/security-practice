package example.security_practice.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ErrorResponseEntity {

    private int httpStatus;
    private String errorName;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponseEntity.from(errorCode));
    }

    public static ErrorResponseEntity from(ErrorCode errorCode) {
        return ErrorResponseEntity.builder()
                .httpStatus(errorCode.getHttpStatus().value())
                .errorName(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }
}
