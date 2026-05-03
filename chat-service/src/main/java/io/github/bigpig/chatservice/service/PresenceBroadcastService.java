package io.github.bigpig.chatservice.service;

import io.github.bigpig.chatservice.dto.response.PresenceUpdateDto;
import io.github.bigpig.chatservice.entity.Chat;
import io.github.bigpig.chatservice.entity.ChatParticipant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresenceBroadcastService {

    private final ChatParticipantService chatParticipantService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Async
    public void broadcastToAllChats(Long userId, String status) {
        List<Chat> chats = chatParticipantService
                .getChatParticipants(userId).stream().map(ChatParticipant::getChat).toList();

        PresenceUpdateDto presenceUpdateDto = PresenceUpdateDto.builder()
                .userId(userId)
                .status(status)
                .build();

        for (Chat chat : chats) {
            log.info("User {} get status {}", userId, status);
            simpMessagingTemplate.convertAndSend("/topic/chat/" + chat.getId() + "/status", presenceUpdateDto);
        }
    }

}
