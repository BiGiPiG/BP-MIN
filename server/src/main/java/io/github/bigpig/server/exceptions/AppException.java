package io.github.bigpig.server.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
class AppException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    protected AppException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
