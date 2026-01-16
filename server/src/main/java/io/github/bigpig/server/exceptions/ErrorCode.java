package io.github.bigpig.server.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS", "This username is already taken", HttpStatusCode.valueOf(409)),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "This email is already used", HttpStatusCode.valueOf(409)),
    USER_NOT_FOUND("USER_NOT_FOUND", "User is not found by this id", HttpStatusCode.valueOf(404)),
    CHAT_NOT_FOUND("CHAT_NOT_FOUND", "Chat is not found by this message", HttpStatusCode.valueOf(404)),
    MESSAGE_NOT_FOUND("MESSAGE_NOT_FOUND", "Message is not found by this id", HttpStatusCode.valueOf(404)),
    CANNOT_EDIT_MESSAGE("CANNOT_EDIT_MESSAGE", "It is denied to edit this message", HttpStatusCode.valueOf(404)),
    CANNOT_DELETE_MESSAGE("CANNOT_DELETE_MESSAGE", "It is denied to delete this message", HttpStatusCode.valueOf(404)),
    UNAUTHORIZED("USER_UNAUTHORISED", "This user is unauthorized", HttpStatusCode.valueOf(401)),

    //auth
    JWT_INVALID("JWT_INVALID", "Invalid JWT token", HttpStatusCode.valueOf(401)),
    JWT_EXPIRED("JWT_EXPIRED", "JWT token has expired", HttpStatusCode.valueOf(401)),
    JWT_UNSUPPORTED("JWT_UNSUPPORTED", "Unsupported JWT token", HttpStatusCode.valueOf(401)),
    JWT_MALFORMED("JWT_MALFORMED", "Malformed JWT token", HttpStatusCode.valueOf(401)),
    USER_NOT_FOUND_IN_TOKEN("USER_NOT_FOUND_IN_TOKEN", "User referenced in token does not exist", HttpStatusCode.valueOf(401)),
    AUTH_HEADER_MISSING("AUTH_HEADER_MISSING", "Authorization header is missing or invalid", HttpStatusCode.valueOf(401));

    private final String code;
    private final String message;
    private final HttpStatusCode status;
}
