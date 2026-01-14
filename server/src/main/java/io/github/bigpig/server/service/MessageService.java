package io.github.bigpig.server.service;

import io.github.bigpig.server.dto.message.MessageDto;
import io.github.bigpig.server.entity.chat.Chat;
import io.github.bigpig.server.entity.chat.ChatParticipant;
import io.github.bigpig.server.entity.chat.Message;
import io.github.bigpig.server.exceptions.AppException;
import io.github.bigpig.server.exceptions.ErrorCode;
import io.github.bigpig.server.repository.ChatRepository;
import io.github.bigpig.server.repository.MessageRepository;
import io.github.bigpig.server.util.ChatMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final ChatParticipantService chatParticipantService;

    public List<MessageDto> getHistory(Long chatId) {
        return messageRepository.findByChatId(chatId).stream().map(chatMessageMapper::toMessageDto).toList();
    }

    public MessageDto save(MessageDto messageDto) {
        Chat chat = chatRepository.findById(messageDto.chatId())
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_NOT_FOUND));

        ChatParticipant chatParticipant = chatParticipantService
                .findChatParticipantByChatIdAndUserId(messageDto.chatId(), messageDto.senderId());

        Message chatMessage = new Message(chat, chatParticipant, messageDto.content());
        return chatMessageMapper.toMessageDto(messageRepository.save(chatMessage));
    }
}
