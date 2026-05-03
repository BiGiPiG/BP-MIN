package io.github.bigpig.userservice.exceptions;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends UserServiceException {
    public AccessDeniedException(String message) {
        super(message, "ACCESS_DENIED", HttpStatus.FORBIDDEN);
    }
}
