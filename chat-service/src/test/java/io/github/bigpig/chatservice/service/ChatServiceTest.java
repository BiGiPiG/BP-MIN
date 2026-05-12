package io.github.bigpig.chatservice.service;

import io.github.bigpig.chatservice.dto.ParticipantInfo;
import io.github.bigpig.chatservice.dto.events.ChatCreatedEvent;
import io.github.bigpig.chatservice.dto.request.CreateChatRequest;
import io.github.bigpig.chatservice.dto.response.ChatDto;
import io.github.bigpig.chatservice.dto.response.MessageDto;
import io.github.bigpig.chatservice.entity.*;
import io.github.bigpig.chatservice.exception.ChatAccessDeniedException;
import io.github.bigpig.chatservice.exception.ChatNotFoundException;
import io.github.bigpig.chatservice.repository.ChatRepository;
import io.github.bigpig.chatservice.utils.MessageMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private UserServiceClient userServiceClient;
    @Mock private ChatRepository chatRepository;
    @Mock private ChatParticipantService chatParticipantService;
    @Mock private MessageMapper messageMapper;
    @InjectMocks private ChatService chatService;

    private static final long USER_ID = 1L;
    private static final long CHAT_ID_1 = 1L;
    private static final long CHAT_ID_2 = 2L;

    @Test
    @DisplayName("Should return list of chat dtos")
    void getChatsReturnListOfChatDtos() {
        //Arrange
        Chat chat1 = new Chat();
        chat1.setId(CHAT_ID_1);
        Chat chat2 = new Chat();
        chat2.setId(CHAT_ID_2);

        ChatParticipant chatParticipant1 = new ChatParticipant(chat1, USER_ID);
        chat1.addParticipant(chatParticipant1);
        ChatParticipant chatParticipant2 = new ChatParticipant(chat2, USER_ID);
        chat2.addParticipant(chatParticipant2);

        when(chatParticipantService.getChatParticipants(USER_ID))
                .thenReturn(List.of(chatParticipant1, chatParticipant2));

        ParticipantInfo info = new ParticipantInfo(USER_ID, "big", "pig", IconColor.BI_COLOR_FUCHSIA_AMBER);
        when(userServiceClient.fetchParticipantInfosMap(List.of(USER_ID))).thenReturn(Map.of(1L, info));

        //Act
        List<ChatDto> result = chatService.getChats(USER_ID);

        //Assert
        assertThat(result).hasSize(2)
                .extracting(ChatDto::id)
                .containsExactlyInAnyOrder(CHAT_ID_1, CHAT_ID_2);

        assertThat(result)
                .extracting(ChatDto::participantInfo)
                .allSatisfy(infos ->
                        assertThat(infos).anyMatch(i -> i.nickname().equals("big"))
                );

        verify(chatParticipantService).getChatParticipants(USER_ID);
        verify(userServiceClient).fetchParticipantInfosMap(List.of(USER_ID));
        verifyNoMoreInteractions(chatParticipantService, userServiceClient);
    }

    @Test
    @DisplayName("Should return empty list of chat dtos")
    void getChatsReturnEmptyList() {
        //Arrange

        when(chatParticipantService.getChatParticipants(USER_ID))
                .thenReturn(List.of());

        //Act
        List<ChatDto> result = chatService.getChats(USER_ID);

        //Assert
        assertThat(result).isEmpty();

        verify(chatParticipantService).getChatParticipants(USER_ID);
        verifyNoMoreInteractions(chatParticipantService);
    }

    @Test
    @DisplayName("Should return chat")
    void getChatById() {
        //Arrange
        Chat chat = new Chat();
        chat.setId(CHAT_ID_1);

        when(chatRepository.findById(CHAT_ID_1)).thenReturn(Optional.of(chat));

        //Act
        Optional<Chat> result = chatService.getChatById(CHAT_ID_1);

        //Assert
        assertThat(result).hasValue(chat);

        verify(chatRepository).findById(CHAT_ID_1);
        verifyNoMoreInteractions(chatRepository);
    }

    @Test
    @DisplayName("Should return empty chat")
    void getEmptyChatById() {
        //Arrange
        when(chatRepository.findById(CHAT_ID_1)).thenReturn(Optional.empty());

        //Act
        Optional<Chat> result = chatService.getChatById(CHAT_ID_1);

        //Assert
        assertThat(result).isEmpty();

        verify(chatRepository).findById(CHAT_ID_1);
        verifyNoMoreInteractions(chatRepository);
    }

    @Test
    @DisplayName("Should return correct chat dto")
    void createChat() {
        //Arrange
        CreateChatRequest request = CreateChatRequest.builder()
                .type(ChatType.DIRECT)
                .title(null)
                .participantIds(List.of(USER_ID))
                .build();

        Chat chat = new Chat();
        chat.setId(CHAT_ID_1);
        ChatParticipant chatParticipant = new ChatParticipant(chat, USER_ID);
        chat.addParticipant(chatParticipant);

        when(chatRepository.save(any(Chat.class)))
                .thenAnswer(invocation -> {
                    Chat saved = invocation.getArgument(0);
                    saved.setId(CHAT_ID_1);
                    return saved;
                });
        ParticipantInfo info = new ParticipantInfo(USER_ID, "big", "pig", IconColor.BI_COLOR_FUCHSIA_AMBER);
        when(userServiceClient.fetchParticipantInfosList(List.of(USER_ID))).thenReturn(List.of(info));

        //Act
        ChatDto result = chatService.createChat(request);

        //Assert
        assertThat(result.id()).isEqualTo(CHAT_ID_1);
        assertThat(result.type()).isEqualTo(request.type());
        assertThat(result.title()).isEqualTo(request.title());
        assertThat(result.participantInfo()).isEqualTo(List.of(info));

        verify(chatRepository).save(any(Chat.class));
        verify(userServiceClient).fetchParticipantInfosList(request.participantIds());
        verify(eventPublisher).publishEvent(any(ChatCreatedEvent.class));
    }

    @Test
    @DisplayName("Should return correct list of messages")
    void getHistory() {
        //Arrange
        Chat chat = new Chat();
        chat.setId(CHAT_ID_1);
        ChatParticipant chatParticipant = new ChatParticipant(chat, USER_ID);
        chat.addParticipant(chatParticipant);

        Message message = new Message();
        message.setId(1L);
        message.setContent("Test");
        message.setChat(chat);

        chat.setMessages(List.of(message));

        when(chatRepository.findById(CHAT_ID_1)).thenReturn(Optional.of(chat));

        MessageDto expectedDto = new MessageDto(
                1L,                              // id
                CHAT_ID_1,                          // chatId
                USER_ID,                            // senderId
                "Test",                             // content
                LocalDateTime.of(2024, 1, 15, 14, 30), // shortSentAt (HH:mm → "14:30")
                "15.01.2024 14:30",                 // fullSentAt
                false                               // isRead
        );

        when(messageMapper.toMessageDto(message)).thenReturn(expectedDto);

        //Act
        List<MessageDto> result = chatService.getHistory(CHAT_ID_1, USER_ID);

        //Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(expectedDto);

        // Verify interactions
        verify(chatRepository).findById(CHAT_ID_1);
        verify(messageMapper).toMessageDto(message);
        verifyNoMoreInteractions(chatRepository, messageMapper);
    }

    @Test
    @DisplayName("Should throw ChatNotFoundException")
    void getHistoryThrowChatNotFoundException() {
        //Arrange
        when(chatRepository.findById(CHAT_ID_1)).thenThrow(new ChatNotFoundException("Chat with this id not found"));

        // Act & Assert
        assertThatThrownBy(() -> chatService.getHistory(CHAT_ID_1, USER_ID))
                .isInstanceOf(ChatNotFoundException.class)
                .hasMessage("Chat with this id not found");
        verifyNoInteractions(messageMapper);
    }

    @Test
    @DisplayName("Should throw ChatAccessDeniedException")
    void getHistoryThrowChatAccessDeniedException() {
        //Arrange
        Chat chat = new Chat();
        chat.setId(CHAT_ID_1);
        ChatParticipant chatParticipant = new ChatParticipant(chat, 2L);
        chat.addParticipant(chatParticipant);

        when(chatRepository.findById(CHAT_ID_1)).thenReturn(Optional.of(chat));

        //Act & Assert
        assertThatThrownBy(() -> chatService.getHistory(CHAT_ID_1, USER_ID))
                .isInstanceOf(ChatAccessDeniedException.class)
                .hasMessage("Access denied: user is not a participant of this chat");
        verifyNoInteractions(messageMapper);
    }

    @Test
    @DisplayName("Should return chat dto")
    void createChatDto() {
        //Arrange
        Chat chat = new Chat();
        chat.setId(CHAT_ID_1);

        ChatParticipant chatParticipant1 = new ChatParticipant(chat, USER_ID);
        chat.addParticipant(chatParticipant1);

        ParticipantInfo info = new ParticipantInfo(USER_ID, "big", "pig", IconColor.BI_COLOR_FUCHSIA_AMBER);

        //Act
        ChatDto result = chatService.createChatDto(chat, List.of(info));

        //Assert
        assertThat(result.id()).isEqualTo(CHAT_ID_1);
        assertThat(result.unread()).isFalse();
        assertThat(result.participantInfo()).isEqualTo(List.of(info));
        assertThat(result.type()).isEqualTo(chat.getType());
        assertThat(result.title()).isEqualTo(chat.getTitle());
        assertThat(result.lastActivity()).isNotNull();
    }
}