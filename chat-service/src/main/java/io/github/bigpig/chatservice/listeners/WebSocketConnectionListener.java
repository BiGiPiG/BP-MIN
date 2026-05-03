package io.github.bigpig.chatservice.listeners;

import io.github.bigpig.chatservice.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketConnectionListener {

    private final PresenceService presenceService;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttrs = accessor.getSessionAttributes();

        String username = null;
        if (sessionAttrs != null) {
            Long userId = (Long) sessionAttrs.get("userId");
            username = (String) sessionAttrs.get("username");
            presenceService.onUserDisconnect(userId);
        }
        log.info("Connection broke for user: {}", username);

    }
}