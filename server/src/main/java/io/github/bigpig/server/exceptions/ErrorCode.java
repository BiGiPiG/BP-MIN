package io.github.bigpig.server.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS", "This username is already taken", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "This email is already used", HttpStatus.CONFLICT),
    USER_NOT_FOUND("USER_NOT_FOUND", "User is not found by this id", HttpStatus.NOT_FOUND),
    CHAT_NOT_FOUND("CHAT_NOT_FOUND", "Chat is not found by this message", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
