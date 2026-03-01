package auth_service.exception;

import org.springframework.http.HttpStatus;

public class InvalidAccessTokenException extends AuthServiceException {

    public InvalidAccessTokenException(String message) {
        super(message, "TOKEN_INVALID", HttpStatus.UNAUTHORIZED);
    }

}
