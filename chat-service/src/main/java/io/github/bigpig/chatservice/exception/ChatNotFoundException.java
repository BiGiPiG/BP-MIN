package io.github.bigpig.chatservice.exception;

import org.springframework.http.HttpStatus;

public class ChatNotFoundException extends ChatServiceException {
    public ChatNotFoundException(String message) {
        super(message, "CHAT_NOT_FOUND", HttpStatus.BAD_REQUEST);
    }
}
