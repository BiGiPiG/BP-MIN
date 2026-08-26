package io.github.bigpig.cloudapigateway.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Отказ, принятый на периметре. Бросается фильтрами; рендерится
 * в {@link GatewayErrorWebExceptionHandler}.
 */
@Getter
public class GatewayException extends RuntimeException {

    private final HttpStatus status;
    private final ApiErrorCode code;

    public GatewayException(HttpStatus status, ApiErrorCode code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
}
