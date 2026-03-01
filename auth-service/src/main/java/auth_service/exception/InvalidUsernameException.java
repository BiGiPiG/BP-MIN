package auth_service.exception;

import org.springframework.http.HttpStatus;

public class InvalidUsernameException extends AuthServiceException {

    public InvalidUsernameException(String message) {
        super(message, "USERNAME_INVALID", HttpStatus.BAD_REQUEST);
    }

}
