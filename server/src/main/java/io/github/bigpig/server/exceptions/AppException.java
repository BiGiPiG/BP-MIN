package io.github.bigpig.server.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;


@Getter
public class AppException extends RuntimeException {
    private final HttpStatusCode status;
    private final String errorCode;

    public AppException(String message, HttpStatusCode status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
        this.errorCode = errorCode.getCode();
    }
}
