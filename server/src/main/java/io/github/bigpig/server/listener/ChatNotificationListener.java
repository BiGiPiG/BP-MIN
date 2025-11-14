package io.github.bigpig.server.listener;

import io.github.bigpig.server.dto.ChatDto;
import io.github.bigpig.server.entity.Chat;
import io.github.bigpig.server.entity.ChatParticipant;
import io.github.bigpig.server.event.ChatCreatedEvent;
import io.github.bigpig.server.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatNotificationListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @EventListener
    public void createdChatEventListener(ChatCreatedEvent event) {

        Chat chat = event.getChat();
        ChatDto chatDto = chatService.getChatDto(chat);

        for (ChatParticipant participant : chat.getParticipants()) {
            long userId = participant.getUser().getId();
            messagingTemplate.convertAndSend("/topic/user/" + userId + "/chats", chatDto);
            log.debug("Notification sent for chat {} to user {}", chatDto.id(), userId);
        }
    }
}
