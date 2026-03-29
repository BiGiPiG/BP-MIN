package io.github.bigpig.chatservice.interceptor;

import io.github.bigpig.chatservice.exception.AuthenticationErrorHandler;
import io.github.bigpig.chatservice.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;
    private final AuthenticationErrorHandler errorHandler;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String accessToken = authHeader.substring(7);

        try {
            if (!jwtService.isValid(accessToken)) {
                return false;
            }

            String username = jwtService.extractUsername(accessToken);
            Long userId = jwtService.extractUserId(accessToken);

            if (username == null || userId == null) {
                log.error("WebSocket handshake failed: Invalid token claims");
                errorHandler.sendErrorResponse(response, HttpStatus.UNAUTHORIZED);
                return false;
            }

            attributes.put("username", username);
            attributes.put("userId", userId);
            return true;

        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired for user: {}", e.getClaims().getSubject());
            errorHandler.sendErrorResponse(response, HttpStatus.UNAUTHORIZED);
            return false;

        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token");
            errorHandler.sendErrorResponse(response, HttpStatus.BAD_REQUEST);
            return false;

        } catch (SignatureException e) {
            log.warn("Signature exception");
            errorHandler.sendErrorResponse(response, HttpStatus.BAD_REQUEST);
            return false;

        } catch (Exception e) {
            log.error("Unexpected error during JWT authentication", e);
            errorHandler.sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR);
            return false;
        }


    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               @Nullable Exception exception) {
        if (exception != null) {
            log.error("WebSocket handshake completed with exception", exception);
        }
    }
}
