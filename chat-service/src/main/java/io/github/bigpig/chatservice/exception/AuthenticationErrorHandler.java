package io.github.bigpig.chatservice.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationErrorHandler {

    public void sendErrorResponse(ServerHttpResponse response, HttpStatus status) {
        response.setStatusCode(status);
    }
}