package io.github.bigpig.auth_service.exception;

import io.github.bigpig.auth_service.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationErrorHandler {

    private final ObjectMapper objectMapper;

    public void sendErrorResponse(HttpServletResponse response,
                          HttpStatus status,
                          String errorCode,
                          String message) throws IOException {

        ErrorResponse error = ErrorResponse.of(status, errorCode, message);

        response.setStatus(error.status());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), error);
    }
}
