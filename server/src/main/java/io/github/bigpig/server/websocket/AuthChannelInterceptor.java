package io.github.bigpig.server.websocket;

import io.github.bigpig.server.entity.auth.User;
import io.github.bigpig.server.service.JwtService;
import io.github.bigpig.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtService.extractUsername(token);
                UserDetails userDetails = userService.loadUserByUsername(username);

                if (jwtService.isValid(token, userDetails)) {
                    Optional<User> userOptional = userService.findByUsername(username);
                    Long userId;
                    if (userOptional.isPresent()) {
                        userId = userOptional.get().getId();
                    } else {
                        throw new RuntimeException("User not found");
                    }
                    Principal user = () -> String.valueOf(userId);
                    accessor.setUser(user);
                } else {
                    throw new RuntimeException("Token is invalid");
                }
            }
        }
        return message;
    }
}
