package io.github.bigpig.chatservice.service;

import io.github.bigpig.chatservice.dto.request.EditMessageDto;
import io.github.bigpig.chatservice.dto.response.MessageDto;
import io.github.bigpig.chatservice.entity.Chat;
import io.github.bigpig.chatservice.entity.ChatParticipant;
import io.github.bigpig.chatservice.entity.Message;
import io.github.bigpig.chatservice.exception.ChatNotFoundException;
import io.github.bigpig.chatservice.exception.MessageNotFoundException;
import io.github.bigpig.chatservice.exception.MessageAccessDeniedException;
import io.github.bigpig.chatservice.repository.MessageRepository;
import io.github.bigpig.chatservice.utils.MessageChecker;
import io.github.bigpig.chatservice.utils.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ChatService chatService;
    private final MessageRepository messageRepository;
    private final ChatParticipantService chatParticipantService;
    private final MessageMapper messageMapper;
    private final Map<String, MessageChecker> messageCheckers;


    @Transactional
    public MessageDto createMessage(MessageDto messageDto) {
        Chat chat = chatService.getChatById(messageDto.chatId())
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        ChatParticipant chatParticipant = chatParticipantService
                .findChatParticipantByChatIdAndUserId(messageDto.chatId(), messageDto.senderId());

        Message chatMessage = new Message(chat, chatParticipant, messageDto.content());
        return messageMapper.toMessageDto(messageRepository.save(chatMessage));
    }

    public Long readMessage(Long messageId, Long editorId) {
        Message message = messageRepository.findById(messageId).orElseThrow(
                () -> new MessageNotFoundException("Message not found")
        );

        if (messageCheckers.get("messageReadChecker").checkErrors(editorId, message)) {
            throw new MessageAccessDeniedException("Access denied to read");
        }

        message.read();

        messageRepository.save(message);
        return message.getChatParticipant().getUserId();
    }

    @Transactional
    public MessageDto updateMessage(Long messageId, Long editorId, EditMessageDto editMessage) {
        Message message = messageRepository.findById(messageId).orElseThrow(
                () -> new MessageNotFoundException("Message not found")
        );

        if (messageCheckers.get("messageEditChecker").checkErrors(editorId, message)) {
            throw new MessageAccessDeniedException("Access denied to edit");
        }

        message.setContent(editMessage.newContent());

        Message editedMessage = messageRepository.save(message);
        return messageMapper.toMessageDto(editedMessage);
    }

    @Transactional
    public void deleteMessage(Long messageId, Long deleterId) {
        Optional<Message> message = messageRepository.findById(messageId);

        if (message.isEmpty()) {
            return;
        }

        Message messageToDelete = message.get();

        if (messageCheckers.get("messageDeleteChecker").checkErrors(deleterId, messageToDelete)) {
            throw new MessageAccessDeniedException("Access denied to delete");
        }

        messageRepository.deleteById(messageId);
    }
}
