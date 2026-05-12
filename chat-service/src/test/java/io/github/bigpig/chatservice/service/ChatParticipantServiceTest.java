package io.github.bigpig.chatservice.service;

import io.github.bigpig.chatservice.entity.Chat;
import io.github.bigpig.chatservice.entity.ChatParticipant;
import io.github.bigpig.chatservice.repository.ChatParticipantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatParticipantService tests")
class ChatParticipantServiceTest {

    @Mock
    ChatParticipantRepository chatParticipantRepository;

    @InjectMocks
    ChatParticipantService chatParticipantService;

    @Test
    @DisplayName("Should return list of chatParticipants")
    void getChatParticipants() {
        //Arrange
        long userId = 1L;
        ChatParticipant chatParticipant1 = new ChatParticipant();
        chatParticipant1.setUserId(userId);
        ChatParticipant chatParticipant2 = new ChatParticipant();
        chatParticipant2.setUserId(userId);
        List<ChatParticipant> expected = List.of(chatParticipant1, chatParticipant2);

        when(chatParticipantRepository.findByUserId(userId)).thenReturn(expected);

        //Act
        List<ChatParticipant> result = chatParticipantService.getChatParticipants(userId);

        //Assert
        assertThat(result).isEqualTo(expected);
        verify(chatParticipantRepository).findByUserId(userId);
        verifyNoMoreInteractions(chatParticipantRepository);
    }

    @Test
    @DisplayName("Should return chatParticipant")
    void findChatParticipantByChatIdAndUserId() {
        //Arrange
        long userId = 1L;
        long chatId = 1L;

        Chat chat = new Chat();
        chat.setId(chatId);

        ChatParticipant expected = new ChatParticipant();
        expected.setUserId(userId);
        expected.setChat(chat);

        when(chatParticipantRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(expected);

        //Act
        ChatParticipant result = chatParticipantService.findChatParticipantByChatIdAndUserId(chatId, userId);

        //Assert
        assertThat(result).isEqualTo(expected);
        verify(chatParticipantRepository).findByChatIdAndUserId(chatId, userId);
        verifyNoMoreInteractions(chatParticipantRepository);
    }

    @Test
    @DisplayName("Should return null list of chatParticipants")
    void getNullChatParticipants() {
        //Arrange
        long userId = 1L;
        when(chatParticipantRepository.findByUserId(userId)).thenReturn(null);

        //Act
        List<ChatParticipant> result = chatParticipantService.getChatParticipants(userId);

        //Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return null chatParticipant")
    void findNullChatParticipantByChatIdAndUserId() {
        //Arrange
        long userId = 1L;
        long chatId = 1L;

        when(chatParticipantRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(null);

        //Act
        ChatParticipant result = chatParticipantService.findChatParticipantByChatIdAndUserId(chatId, userId);

        //Assert
        assertThat(result).isNull();
    }

}

