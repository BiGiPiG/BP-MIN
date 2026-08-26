package io.github.bigpig.cloudapigateway.error;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Формат ошибки внешнего API. Обязан совпадать со схемой {@code ErrorResponse}
 * в api/openapi.yaml и с одноимёнными записями в auth-service и chat-service.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String code,
        Object details
) {

    public static ErrorResponse of(HttpStatus status, ApiErrorCode code, String details) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                code.name(),
                details
        );
    }
}
