package io.github.bigpig.chatservice.listeners;

import io.github.bigpig.chatservice.dto.ParticipantInfo;
import io.github.bigpig.chatservice.dto.events.ChatCreatedEvent;
import io.github.bigpig.chatservice.dto.response.ChatDto;
import io.github.bigpig.chatservice.service.ChatService;
import io.github.bigpig.chatservice.service.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatNotificationListener {

    private final ChatService chatService;
    private final UserServiceClient userServiceClient;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void onNewChat(ChatCreatedEvent event) {
        List<ParticipantInfo> infosList = userServiceClient.fetchParticipantInfosList(event.getParticipantsIds());
        ChatDto chatDto = chatService.createChatDto(event.getChat(), infosList);
        for (Long userId : event.getParticipantsIds()) {
            try {
                String destination = "/topic/user/" + userId + "/chats";
                messagingTemplate.convertAndSend(destination, chatDto);
                log.info("Sent WS notification to user {} for chat {}", userId, chatDto.id());
            } catch (Exception e) {
                log.warn("Failed to notify user {} about chat {}", userId, chatDto.id(), e);
            }
        }
    }
}
