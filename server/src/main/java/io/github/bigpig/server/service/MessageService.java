package io.github.bigpig.server.service;

import io.github.bigpig.server.dto.message.EditMessageDto;
import io.github.bigpig.server.dto.message.MessageDto;
import io.github.bigpig.server.entity.chat.Chat;
import io.github.bigpig.server.entity.chat.ChatParticipant;
import io.github.bigpig.server.entity.chat.Message;
import io.github.bigpig.server.exceptions.AppException;
import io.github.bigpig.server.exceptions.ErrorCode;
import io.github.bigpig.server.repository.ChatRepository;
import io.github.bigpig.server.repository.MessageRepository;
import io.github.bigpig.server.util.ChatMessageMapper;
import io.github.bigpig.server.util.message.IMessageDeleteChecker;
import io.github.bigpig.server.util.message.IMessageEditChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final ChatParticipantService chatParticipantService;
    private final IMessageEditChecker messageEditChecker;
    private final IMessageDeleteChecker messageDeleteChecker;

    public List<MessageDto> getHistory(Long chatId) {
        return messageRepository.findByChatId(chatId).stream().map(chatMessageMapper::toMessageDto).toList();
    }

    @Transactional
    public MessageDto editMessage(Long messageId, Long editorId, EditMessageDto editMessage) {
        Message message = messageRepository.findById(messageId).orElseThrow(
                () -> new AppException(ErrorCode.MESSAGE_NOT_FOUND)
        );

        if (!messageEditChecker.canEdit(editorId, message)) {
            throw new AppException(ErrorCode.CANNOT_EDIT_MESSAGE);
        }

        message.setContent(editMessage.newContent());

        Message editedMessage = messageRepository.save(message);
        return chatMessageMapper.toMessageDto(editedMessage);
    }

    @Transactional
    public void deleteMessage(Long messageId, Long deleterId) {
        Message message = messageRepository.findById(messageId).orElseThrow(
                () -> new AppException(ErrorCode.MESSAGE_NOT_FOUND)
        );

        if (!messageDeleteChecker.canDelete(deleterId, message)) {
            throw new AppException(ErrorCode.CANNOT_DELETE_MESSAGE);
        }

        messageRepository.deleteById(messageId);
    }

    public MessageDto createMessage(MessageDto messageDto) {
        Chat chat = chatRepository.findById(messageDto.chatId())
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_NOT_FOUND));

        ChatParticipant chatParticipant = chatParticipantService
                .findChatParticipantByChatIdAndUserId(messageDto.chatId(), messageDto.senderId());

        Message chatMessage = new Message(chat, chatParticipant, messageDto.content());
        return chatMessageMapper.toMessageDto(messageRepository.save(chatMessage));
    }
}
