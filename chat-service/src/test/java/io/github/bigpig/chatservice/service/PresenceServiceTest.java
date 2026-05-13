package io.github.bigpig.chatservice.service;

import io.github.bigpig.chatservice.dto.events.UserChangedStatusEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PresenceServiceTest {

    @Mock private KafkaTemplate<Long, UserChangedStatusEvent> kafkaTemplate;
    @Mock private PresenceBroadcastService presenceBroadcastService;

    @InjectMocks private PresenceService presenceService;

    private final long USER_ID = 1L;

    @Test
    @DisplayName("onUserConnect: Should send UserChangedStatusEvent with status 'online' and call broadcast")
    void onUserConnect() {
        //Act
        presenceService.onUserConnect(USER_ID);

        //Assert
        verify(kafkaTemplate).send(eq("user-changed-status-topic"), eq(USER_ID), any(UserChangedStatusEvent.class));
        verify(presenceBroadcastService).broadcastToAllChats(USER_ID, "online");
    }

    @Test
    @DisplayName("onUserDisconnect: Should send UserChangedStatusEvent with status 'offline' and call broadcast")
    void onUserDisconnect() {
        //Act
        presenceService.onUserDisconnect(USER_ID);

        //Assert
        verify(kafkaTemplate).send(eq("user-changed-status-topic"),eq(USER_ID), any(UserChangedStatusEvent.class));
        verify(presenceBroadcastService).broadcastToAllChats(USER_ID, "offline");
    }

}