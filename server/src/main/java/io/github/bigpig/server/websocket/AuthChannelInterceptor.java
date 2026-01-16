package io.github.bigpig.server.websocket;

import io.github.bigpig.server.exceptions.AuthException;
import io.github.bigpig.server.exceptions.ErrorCode;
import io.github.bigpig.server.service.CustomUserDetailsService;
import io.github.bigpig.server.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtService.extractUsername(token);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                if (jwtService.isValid(token, userDetails)) {
                    accessor.setUser((Principal) userDetails);
                } else {
                    throw new AuthException(ErrorCode.JWT_INVALID);
                }
            }
        }
        return message;
    }
}
