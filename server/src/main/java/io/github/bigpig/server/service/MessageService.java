package io.github.bigpig.server.service;

import io.github.bigpig.server.dto.chat.MessageDto;
import io.github.bigpig.server.entity.chat.Chat;
import io.github.bigpig.server.repository.ChatRepository;
import io.github.bigpig.server.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;

    public List<MessageDto> getHistory(Long chatId) {
        return messageRepository.findByChatId(chatId).stream().map(MessageDto::of).toList();
    }

    public MessageDto save(MessageDto messageDto) {
        Chat chat = chatRepository.findById(messageDto.chatId()).get();
        Chat.ChatMessage chatMessage = new Chat.ChatMessage(
            chat, messageDto.senderId(), messageDto.content()
        );
        return MessageDto.of(messageRepository.save(chatMessage));
    }
}
