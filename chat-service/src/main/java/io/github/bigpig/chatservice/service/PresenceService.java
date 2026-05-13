package io.github.bigpig.chatservice.service;

import io.github.bigpig.chatservice.dto.events.UserChangedStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresenceService {

    private final KafkaTemplate<Long, UserChangedStatusEvent> kafkaTemplate;
    private final PresenceBroadcastService presenceBroadcastService;
    private final static String ONLINE_STATUS = "online";
    private final static String OFFLINE_STATUS = "offline";

    public void onUserConnect(Long id) {
        UserChangedStatusEvent event = new UserChangedStatusEvent(id, ONLINE_STATUS);
        kafkaTemplate.send("user-changed-status-topic", id, event);
        log.info("connect event was sent for user: {}", id);
        presenceBroadcastService.broadcastToAllChats(id, ONLINE_STATUS);
    }

    public void onUserDisconnect(Long id) {
        UserChangedStatusEvent event = new UserChangedStatusEvent(id, OFFLINE_STATUS);
        kafkaTemplate.send("user-changed-status-topic", id, event);
        log.info("disconnect event was sent for user: {}", id);
        presenceBroadcastService.broadcastToAllChats(id, OFFLINE_STATUS);
    }
}
