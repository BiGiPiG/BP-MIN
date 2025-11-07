package io.github.bigpig.server.exceptions;

import io.github.bigpig.server.dto.ExceptionResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionResponseDto> handleAuthException(AuthException ex) {
        return new ResponseEntity<>(new ExceptionResponseDto(ex.getMessage(), ex.getErrorCode()), ex.getStatus());
    }

}
