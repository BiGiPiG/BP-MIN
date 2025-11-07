package io.github.bigpig.server.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS", "This username is already taken", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "This email is already used", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
