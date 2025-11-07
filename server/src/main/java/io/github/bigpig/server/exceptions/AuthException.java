package io.github.bigpig.server.exceptions;

public class AuthException extends AppException {
    public AuthException(ErrorCode error) {
        super(error.getMessage(), error.getStatus(), error.getCode());
    }
}
