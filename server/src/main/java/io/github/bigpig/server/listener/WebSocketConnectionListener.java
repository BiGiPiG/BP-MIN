package io.github.bigpig.server.listener;


import io.github.bigpig.server.entity.user.User;
import io.github.bigpig.server.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketConnectionListener {

    private final PresenceService presenceService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        User user = (User) event.getUser();
        if (user == null) {
            log.error("Connection error. User is null!");
            return;
        }
        log.info("Connection established.");
        presenceService.changePresence(user.getId(), "ONLINE");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        User user = (User) event.getUser();
        if (user == null) {
            log.info("Disconnection error. User is null!");
            return;
        }
        log.info("Connection closed");
        presenceService.changePresence(user.getId(), "OFFLINE");
    }
}
