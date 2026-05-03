package io.github.bigpig.auth_service.dto.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String code,
        Object details
) {
    public static ErrorResponse of(HttpStatus status, String errorCode, String message) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(errorCode)
                .details(message)
                .build();
    }

    public static ErrorResponse ofValidation(HttpStatus status, Map<String, String> fieldErrors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code("VALIDATION_FAILED")
                .details(fieldErrors)
                .build();
    }
}
