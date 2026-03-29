package io.github.bigpig.chatservice.exception;

import org.springframework.http.HttpStatus;

public class MessageAccessDeniedException extends ChatServiceException {
    public MessageAccessDeniedException(String message) {
        super(message, "ACCESS_DENIED", HttpStatus.FORBIDDEN);
    }
}
