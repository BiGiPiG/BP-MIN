package io.github.bigpig.chatservice.service;

import io.github.bigpig.chatservice.entity.ChatParticipant;
import io.github.bigpig.chatservice.repository.ChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;

    public List<ChatParticipant> getChatParticipants(long userId) {
        return chatParticipantRepository.findByUserId(userId);
    }

    public ChatParticipant findChatParticipantByChatIdAndUserId(Long chatId, Long senderId) {
        return chatParticipantRepository.findByChatIdAndUserId(chatId, senderId);
    }
}
