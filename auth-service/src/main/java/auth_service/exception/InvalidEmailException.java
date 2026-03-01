package auth_service.exception;

import org.springframework.http.HttpStatus;

public class InvalidEmailException extends AuthServiceException {

    public InvalidEmailException(String message) {
        super(message, "EMAIL_INVALID", HttpStatus.BAD_REQUEST);
    }

}
