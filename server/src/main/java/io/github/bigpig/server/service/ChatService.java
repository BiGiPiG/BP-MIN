package io.github.bigpig.server.service;

import io.github.bigpig.server.dto.ChatDto;
import io.github.bigpig.server.dto.CreateChatRequestDto;
import io.github.bigpig.server.dto.ParticipantInfo;
import io.github.bigpig.server.entity.Chat;
import io.github.bigpig.server.entity.ChatParticipant;
import io.github.bigpig.server.entity.User;
import io.github.bigpig.server.event.ChatCreatedEvent;
import io.github.bigpig.server.repository.ChatParticipantRepository;
import io.github.bigpig.server.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final UserService userService;
    private final ChatRepository chatRepository;
    private final ApplicationEventPublisher eventPublisher;

    public List<Chat> findChatsByUserId(Long userId) {
        return chatParticipantRepository.findChatsByUserId(userId);
    }

    public ChatDto getChatDto(Chat chat) {
        List<ParticipantInfo> participantInfos = chatParticipantRepository
                .findActiveParticipantsWithNicknamesByChatId(chat.getId());
        return ChatDto.fromChat(chat, participantInfos);
    }

    @Transactional
    public Chat createChat(CreateChatRequestDto requestDto) {
        Chat chat = new Chat(requestDto.type(), requestDto.title());
        for (String username : requestDto.participants()) {
            User participant = userService.findByUsername(username).get();
            ChatParticipant chatParticipant = new ChatParticipant(chat, participant);
            chat.addParticipant(chatParticipant);
        }
        Chat newChat = chatRepository.save(chat);
        eventPublisher.publishEvent(new ChatCreatedEvent(this, newChat));
        return chat;
    }

}
