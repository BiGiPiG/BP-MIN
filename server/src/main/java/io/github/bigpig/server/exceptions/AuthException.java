package io.github.bigpig.server.exceptions;

import org.springframework.http.HttpStatus;

public class AuthException extends AppException {
    public AuthException(ErrorCode error) {
        super(error.getMessage(), (HttpStatus) error.getStatus(), error.getCode());
    }
}
