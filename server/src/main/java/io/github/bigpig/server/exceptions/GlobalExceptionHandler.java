package io.github.bigpig.server.exceptions;

import io.github.bigpig.server.dto.ExceptionResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionResponseDto> handleAuthException(AuthException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(new ExceptionResponseDto(ex.getMessage(), ex.getErrorCode()), ex.getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseDto> handleAppException(AppException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(new ExceptionResponseDto(ex.getMessage(), ex.getErrorCode()), ex.getStatus());
    }

}
