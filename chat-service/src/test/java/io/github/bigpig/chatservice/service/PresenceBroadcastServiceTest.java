package io.github.bigpig.chatservice.service;

import io.github.bigpig.chatservice.dto.response.PresenceUpdateDto;
import io.github.bigpig.chatservice.entity.Chat;
import io.github.bigpig.chatservice.entity.ChatParticipant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresenceBroadcastServiceTest {

    @Mock private ChatParticipantService chatParticipantService;
    @Mock private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks private PresenceBroadcastService presenceBroadcastService;

    private final long USER_ID = 1L;
    private final String STATUS = "online";

    @Test
    @DisplayName("Should broadcast presence update to all user chats")
    void broadcastToAllChats_whenUserHasChats_shouldSendToAllTopics() {
        // Arrange
        Chat chat1 = new Chat();
        chat1.setId(1L);
        Chat chat2 = new Chat();
        chat2.setId(2L);
        Chat chat3 = new Chat();
        chat3.setId(3L);

        List<ChatParticipant> participants = List.of(
                new ChatParticipant(chat1, USER_ID),
                new ChatParticipant(chat2, USER_ID),
                new ChatParticipant(chat3, USER_ID)
        );

        when(chatParticipantService.getChatParticipants(USER_ID)).thenReturn(participants);

        ArgumentCaptor<PresenceUpdateDto> dtoCaptor = ArgumentCaptor.forClass(PresenceUpdateDto.class);
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        presenceBroadcastService.broadcastToAllChats(USER_ID, STATUS);

        // Assert
        verify(chatParticipantService).getChatParticipants(USER_ID);
        verify(simpMessagingTemplate, times(3)).convertAndSend(destinationCaptor.capture(), dtoCaptor.capture());

        List<String> destinations = destinationCaptor.getAllValues();
        assertThat(destinations).containsExactlyInAnyOrder(
                "/topic/chat/1/status",
                "/topic/chat/2/status",
                "/topic/chat/3/status"
        );

        List<PresenceUpdateDto> dtos = dtoCaptor.getAllValues();
        for (PresenceUpdateDto dto : dtos) {
            assertThat(dto.userId()).isEqualTo(USER_ID);
            assertThat(dto.status()).isEqualTo(STATUS);
        }
    }

    @Test
    @DisplayName("Should do nothing when user has no chats")
    void broadcastToAllChats_whenUserHasNoChats_shouldNotSendAnything() {
        // Arrange
        when(chatParticipantService.getChatParticipants(USER_ID)).thenReturn(List.of());

        // Act
        presenceBroadcastService.broadcastToAllChats(USER_ID, STATUS);

        // Assert
        verify(chatParticipantService).getChatParticipants(USER_ID);
        verifyNoInteractions(simpMessagingTemplate);
    }

    @Test
    @DisplayName("Should broadcast with different status values")
    void broadcastToAllChats_withVariousStatuses_shouldSendCorrectStatus() {
        // Arrange
        Chat chat = new Chat();
        chat.setId(42L);
        List<ChatParticipant> participants = List.of(new ChatParticipant(chat, USER_ID));

        when(chatParticipantService.getChatParticipants(USER_ID)).thenReturn(participants);

        ArgumentCaptor<PresenceUpdateDto> dtoCaptor = ArgumentCaptor.forClass(PresenceUpdateDto.class);

        // Act
        presenceBroadcastService.broadcastToAllChats(USER_ID, "offline");

        // Assert
        verify(simpMessagingTemplate).convertAndSend(eq("/topic/chat/42/status"), dtoCaptor.capture());
        assertThat(dtoCaptor.getValue().status()).isEqualTo("offline");
    }

    @Test
    @DisplayName("Should use correct destination format")
    void broadcastToAllChats_shouldUseCorrectTopicFormat() {
        // Arrange
        Chat chat = new Chat();
        chat.setId(999L);
        List<ChatParticipant> participants = List.of(new ChatParticipant(chat, USER_ID));

        when(chatParticipantService.getChatParticipants(USER_ID)).thenReturn(participants);

        // Act
        presenceBroadcastService.broadcastToAllChats(USER_ID, STATUS);

        // Assert
        verify(simpMessagingTemplate).convertAndSend(eq("/topic/chat/999/status"), any(PresenceUpdateDto.class));
    }
}