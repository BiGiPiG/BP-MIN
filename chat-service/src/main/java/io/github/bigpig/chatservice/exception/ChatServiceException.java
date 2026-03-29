package io.github.bigpig.chatservice.exception;

import org.springframework.http.HttpStatus;

public class ChatServiceException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    public ChatServiceException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
