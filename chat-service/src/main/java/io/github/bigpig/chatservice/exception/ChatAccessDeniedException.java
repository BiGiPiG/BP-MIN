package io.github.bigpig.chatservice.exception;

import org.springframework.http.HttpStatus;

public class ChatAccessDeniedException extends ChatServiceException {
    public ChatAccessDeniedException(String message) {
        super(message, "ACCESS_DENIED", HttpStatus.FORBIDDEN);
    }
}
