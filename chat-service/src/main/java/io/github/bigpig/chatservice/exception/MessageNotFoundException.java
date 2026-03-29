package io.github.bigpig.chatservice.exception;

import org.springframework.http.HttpStatus;

public class MessageNotFoundException extends ChatServiceException {
    public MessageNotFoundException(String message) {
        super(message, "MESSAGE_NOT_FOUND", HttpStatus.BAD_REQUEST);
    }
}
