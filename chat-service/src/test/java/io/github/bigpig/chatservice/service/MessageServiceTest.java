package io.github.bigpig.chatservice.service;

import io.github.bigpig.chatservice.dto.request.EditMessageDto;
import io.github.bigpig.chatservice.dto.response.MessageDto;
import io.github.bigpig.chatservice.entity.Chat;
import io.github.bigpig.chatservice.entity.ChatParticipant;
import io.github.bigpig.chatservice.entity.Message;
import io.github.bigpig.chatservice.exception.ChatNotFoundException;
import io.github.bigpig.chatservice.exception.MessageAccessDeniedException;
import io.github.bigpig.chatservice.exception.MessageNotFoundException;
import io.github.bigpig.chatservice.repository.MessageRepository;
import io.github.bigpig.chatservice.utils.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock private ChatService chatService;
    @Mock private ChatParticipantService chatParticipantService;
    @Mock private MessageRepository messageRepository;
    @Mock private MessageMapper messageMapper;
    @Mock private Map<String, MessageChecker> messageCheckers;
    @Mock private MessageReadChecker messageReadChecker;
    @Mock private MessageEditChecker messageEditChecker;
    @Mock private MessageDeleteChecker messageDeleteChecker;

    @InjectMocks private MessageService messageService;

    private final long CHAT_ID = 1L;
    private final long USER_ID = 1L;
    private final long MESSAGE_ID = 1L;

    @Test
    @DisplayName("Should return message dto")
    void createMessage() {
        //Arrange
        Chat chat = new Chat();
        chat.setId(CHAT_ID);
        ChatParticipant participant = new ChatParticipant(chat, USER_ID);
        chat.addParticipant(participant);

        Message message = new Message(chat, participant, "Test message");

        MessageDto expected = MessageDto.builder()
                .id(message.getId())
                .chatId(CHAT_ID)
                .senderId(USER_ID)
                .content(message.getContent())
                .shortSentAt(message.getSentAt())
                .isRead(message.getIsRead())
                .build();

        when(chatService.getChatById(CHAT_ID)).thenReturn(Optional.of(chat));
        when(chatParticipantService
                .findChatParticipantByChatIdAndUserId(CHAT_ID, USER_ID))
                .thenReturn(participant);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(messageMapper.toMessageDto(message)).thenReturn(expected);

        //Act
        MessageDto result = messageService.createMessage(expected);

        //Assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should throw ChatNotFoundException")
    void createMessageThrowChatNotFoundException() {
        //Arrange
        Chat chat = new Chat();
        chat.setId(CHAT_ID);
        ChatParticipant participant = new ChatParticipant(chat, USER_ID);
        chat.addParticipant(participant);

        Message message = new Message(chat, participant, "Test message");

        MessageDto expected = MessageDto.builder()
                .id(message.getId())
                .chatId(CHAT_ID)
                .senderId(USER_ID)
                .content(message.getContent())
                .shortSentAt(message.getSentAt())
                .isRead(message.getIsRead())
                .build();

        when(chatService.getChatById(CHAT_ID)).thenReturn(Optional.empty());
        //Act & Assert
        assertThatThrownBy(() -> messageService.createMessage(expected), "Chat not found")
                .isInstanceOf(ChatNotFoundException.class)
                .hasMessage("Chat not found");

        verify(chatService).getChatById(CHAT_ID);
        verifyNoMoreInteractions(chatService);
    }

    @Test
    @DisplayName("Should return editor id")
    void readMessage() {
        //Arrange
        Chat chat = new Chat();
        chat.setId(CHAT_ID);
        ChatParticipant participant = new ChatParticipant(chat, USER_ID);
        chat.addParticipant(participant);

        Message message = new Message(chat, participant, "Test message");

        when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.of(message));
        when(messageCheckers.get("messageReadChecker")).thenReturn(messageReadChecker);
        when(messageReadChecker.checkErrors(USER_ID, message)).thenReturn(false);

        when(messageRepository.save(any(Message.class))).thenReturn(message);

        //Act
        long result = messageService.readMessage(MESSAGE_ID, USER_ID);

        //Assert
        assertThat(result).isEqualTo(USER_ID);
    }

    @Test
    @DisplayName("Should throw MessageNotFoundException")
    void readMessageThrowMessageNotFoundException() {
        //Arrange
        when(messageRepository.findById(MESSAGE_ID))
                .thenReturn(Optional.empty());

        //Act & Assert
        assertThatThrownBy(() -> messageService.readMessage(MESSAGE_ID, USER_ID), "Message not found")
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessage("Message not found");

        verifyNoMoreInteractions(messageRepository);
    }

    @Test
    @DisplayName("Should throw MessageAccessDeniedException")
    void readMessageThrowMessageAccessDeniedException() {
        //Arrange
        Chat chat = new Chat();
        chat.setId(CHAT_ID);
        ChatParticipant participant = new ChatParticipant(chat, USER_ID);
        chat.addParticipant(participant);

        Message message = new Message(chat, participant, "Test message");

        when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.of(message));
        when(messageCheckers.get("messageReadChecker")).thenReturn(messageReadChecker);
        when(messageReadChecker.checkErrors(USER_ID, message)).thenReturn(true);

        //Act & Assert
        assertThatThrownBy(() -> messageService.readMessage(MESSAGE_ID, USER_ID), "Message access denied")
                .isInstanceOf(MessageAccessDeniedException.class)
                .hasMessage("Access denied to read");

        verifyNoMoreInteractions(messageRepository, messageCheckers, messageReadChecker);
    }

    @Test
    @DisplayName("Should update message and return message dto")
    void updateMessage() {
        //Arrange
        Chat chat = new Chat();
        chat.setId(CHAT_ID);
        ChatParticipant participant = new ChatParticipant(chat, USER_ID);
        chat.addParticipant(participant);

        Message message = new Message(chat, participant, "Test message");

        EditMessageDto editMessageDto =
                EditMessageDto.builder()
                        .messageId(MESSAGE_ID)
                        .chatId(CHAT_ID)
                        .newContent(message.getContent()).build();

        when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.of(message));
        when(messageCheckers.get("messageEditChecker")).thenReturn(messageEditChecker);
        when(messageEditChecker.checkErrors(USER_ID, message)).thenReturn(false);
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        MessageDto expected = MessageDto.builder()
                .id(message.getId())
                .chatId(CHAT_ID)
                .senderId(USER_ID)
                .content(message.getContent())
                .shortSentAt(message.getSentAt())
                .isRead(message.getIsRead())
                .build();

        when(messageMapper.toMessageDto(message)).thenReturn(expected);

        //Act
        MessageDto result = messageService.updateMessage(MESSAGE_ID, USER_ID, editMessageDto);

        //Assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should throw MessageNotFoundException")
    void updateMessageThrowMessageNotFoundException() {
        //Arrange
        Chat chat = new Chat();
        chat.setId(CHAT_ID);
        ChatParticipant participant = new ChatParticipant(chat, USER_ID);
        chat.addParticipant(participant);

        Message message = new Message(chat, participant, "Test message");

        EditMessageDto editMessageDto =
                EditMessageDto.builder()
                        .messageId(MESSAGE_ID)
                        .chatId(CHAT_ID)
                        .newContent(message.getContent()).build();

        when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.empty());

        //Act & Assert
        assertThatThrownBy(() -> messageService.updateMessage(MESSAGE_ID, USER_ID, editMessageDto), "Message not found")
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessage("Message not found");

        verifyNoMoreInteractions(messageRepository);
    }

    @Test
    @DisplayName("Should throw MessageAccessDeniedException")
    void updateMessageThrowMessageAccessDeniedException() {
        //Arrange
        Chat chat = new Chat();
        chat.setId(CHAT_ID);
        ChatParticipant participant = new ChatParticipant(chat, USER_ID);
        chat.addParticipant(participant);

        Message message = new Message(chat, participant, "Test message");

        EditMessageDto editMessageDto =
                EditMessageDto.builder()
                        .messageId(MESSAGE_ID)
                        .chatId(CHAT_ID)
                        .newContent(message.getContent()).build();

        when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.of(message));
        when(messageCheckers.get("messageEditChecker")).thenReturn(messageEditChecker);
        when(messageEditChecker.checkErrors(USER_ID, message)).thenReturn(true);

        //Act & Assert
        assertThatThrownBy(() -> messageService.updateMessage(MESSAGE_ID, USER_ID, editMessageDto), "Message access denied")
                .isInstanceOf(MessageAccessDeniedException.class)
                .hasMessage("Access denied to edit");

        verifyNoMoreInteractions(messageRepository, messageCheckers, messageEditChecker);
    }

    @Test
    @DisplayName("Should delete message")
    void deleteMessage() {
        //Arrange
        Chat chat = new Chat();
        chat.setId(CHAT_ID);
        ChatParticipant participant = new ChatParticipant(chat, USER_ID);
        chat.addParticipant(participant);

        Message message = new Message(chat, participant, "Test message");

        when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.of(message));
        when(messageCheckers.get("messageDeleteChecker")).thenReturn(messageDeleteChecker);
        when(messageDeleteChecker.checkErrors(USER_ID, message)).thenReturn(false);

        //Act
        messageService.deleteMessage(MESSAGE_ID, USER_ID);

        //Assert
        verify(messageRepository).deleteById(MESSAGE_ID);
    }

    @Test
    @DisplayName("Should do nothing")
    void deleteUnknownMessage() {
        //Arrange
        when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.empty());

        //Act
        messageService.deleteMessage(MESSAGE_ID, USER_ID);

        //Assert
        verifyNoMoreInteractions(messageRepository);
    }

    @Test
    @DisplayName("Should throw MessageAccessDeniedException")
    void deleteMessageThrowMessageAccessDeniedException() {
        //Arrange
        Chat chat = new Chat();
        chat.setId(CHAT_ID);
        ChatParticipant participant = new ChatParticipant(chat, USER_ID);
        chat.addParticipant(participant);

        Message message = new Message(chat, participant, "Test message");

        when(messageRepository.findById(MESSAGE_ID)).thenReturn(Optional.of(message));
        when(messageCheckers.get("messageDeleteChecker")).thenReturn(messageDeleteChecker);
        when(messageDeleteChecker.checkErrors(USER_ID, message)).thenReturn(true);

        //Act & Assert
        assertThatThrownBy(() -> messageService.deleteMessage(MESSAGE_ID, USER_ID))
                .isInstanceOf(MessageAccessDeniedException.class)
                .hasMessage("Access denied to delete");

        verifyNoMoreInteractions(messageRepository, messageCheckers, messageDeleteChecker);
    }
}