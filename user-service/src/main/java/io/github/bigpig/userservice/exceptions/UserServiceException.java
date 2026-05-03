package io.github.bigpig.userservice.exceptions;

import org.springframework.http.HttpStatus;

public class UserServiceException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    public UserServiceException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
