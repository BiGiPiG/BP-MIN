package io.github.bigpig.server.service;

import io.github.bigpig.server.dto.chat.ParticipantInfo;
import io.github.bigpig.server.entity.auth.User;
import io.github.bigpig.server.entity.chat.Chat;
import io.github.bigpig.server.repository.ChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;

    public List<Chat> findChatsByUser(User user) {
        return chatParticipantRepository.findChatsByUsername(user.getUsername());
    }

    public List<ParticipantInfo> findActiveParticipantsWithNicknamesByChatId(Long chatId) {
        return chatParticipantRepository.findActiveParticipantsWithNicknamesByChatId(chatId);
    }

}
