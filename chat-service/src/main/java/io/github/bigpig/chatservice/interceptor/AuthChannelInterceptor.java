package io.github.bigpig.chatservice.interceptor;

import io.github.bigpig.chatservice.service.JwtService;
import io.github.bigpig.chatservice.service.PresenceService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final PresenceService presenceService;
    private final JwtService jwtService;

    public AuthChannelInterceptor(@Lazy PresenceService presenceService,
                                  JwtService jwtService) {
        this.presenceService = presenceService;
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authHeaders = accessor.getNativeHeader("Authorization");

            if (authHeaders == null || authHeaders.isEmpty()) {
                log.warn("STOMP CONNECT rejected: Missing Authorization header");
                throw new SecurityException("Missing Authorization header");
            }

            String authHeader = authHeaders.getFirst();
            if (!authHeader.startsWith("Bearer ")) {
                log.warn("STOMP CONNECT rejected: Invalid Authorization header format");
                throw new SecurityException("Invalid Authorization header format");
            }

            String accessToken = authHeader.substring(7);

            try {
                if (!jwtService.isValid(accessToken)) {
                    log.warn("STOMP CONNECT rejected: Invalid token");
                    throw new SecurityException("Invalid token");
                }

                String username = jwtService.extractUsername(accessToken);
                Long userId = jwtService.extractUserId(accessToken);

                if (username == null || userId == null) {
                    log.error("STOMP CONNECT rejected: Invalid token claims");
                    throw new SecurityException("Invalid token claims");
                }

                accessor.getSessionAttributes().put("username", username);
                accessor.getSessionAttributes().put("userId", userId);

                presenceService.onUserConnect(userId);
                log.info("STOMP CONNECT accepted for user: {}, userId={}", username, userId);

            } catch (ExpiredJwtException e) {
                log.warn("JWT token expired for user: {}", e.getClaims().getSubject());
                throw new SecurityException("Token expired");
            } catch (MalformedJwtException | SignatureException e) {
                log.warn("Invalid JWT token: {}", e.getMessage());
                throw new SecurityException("Invalid token format");
            } catch (Exception e) {
                log.error("Unexpected error during STOMP JWT authentication", e);
                throw new SecurityException("Authentication failed");
            }
        }

        return message;
    }
}