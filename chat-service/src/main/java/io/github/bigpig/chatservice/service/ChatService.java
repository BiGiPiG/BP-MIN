package io.github.bigpig.chatservice.service;

import io.github.bigpig.chatservice.dto.ParticipantInfo;
import io.github.bigpig.chatservice.dto.request.CreateChatRequest;
import io.github.bigpig.chatservice.dto.response.ChatDto;
import io.github.bigpig.chatservice.dto.response.MessageDto;
import io.github.bigpig.chatservice.entity.Chat;
import io.github.bigpig.chatservice.entity.ChatParticipant;
import io.github.bigpig.chatservice.exception.ChatAccessDeniedException;
import io.github.bigpig.chatservice.exception.ChatNotFoundException;
import io.github.bigpig.chatservice.repository.ChatRepository;
import io.github.bigpig.chatservice.utils.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserServiceClient userServiceClient;
    private final ChatRepository chatRepository;
    private final ChatParticipantService chatParticipantService;
    private final MessageMapper messageMapper;

    public List<ChatDto> getChats(long userId) {
        List<ChatParticipant> participants = chatParticipantService.getChatParticipants(userId);
        if (participants.isEmpty()) {
            return List.of();
        }

        List<Chat> chats = participants.stream().map(ChatParticipant::getChat).toList();
        List<Long> participantIds = getActiveParticipantIds(chats);

        Map<Long, ParticipantInfo> participantInfos = userServiceClient.fetchParticipantInfos(participantIds);
        return getChatsDto(chats, participantInfos);
    }

    public Optional<Chat> getChatById(long chatId) {
        return chatRepository.findById(chatId);
    }

    public void createChat(CreateChatRequest chatDto) {
        Chat chat = new Chat(chatDto.type(), chatDto.title());
        for (long userId : chatDto.participantIds()) {
            ChatParticipant newChatParticipant = new ChatParticipant(chat, userId);
            chat.addParticipant(newChatParticipant);
        }
        chatRepository.save(chat);
    }

    public List<MessageDto> getHistory(long chatId, long userId) {

        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException("Chat with this id not found"));

        if (!isActiveParticipant(userId, chat)) {
            throw new ChatAccessDeniedException("Access denied: user is not a participant of this chat");
        }

        return chat.getMessages().stream().map(messageMapper::toMessageDto).toList();
    }

    private boolean isActiveParticipant(long userId, Chat chat) {
        return chat.getParticipants().stream()
                .anyMatch(p -> p.getUserId() == userId);
    }

    private List<Long> getActiveParticipantIds(List<Chat> chats) {
        Set<Long> participantIds = new HashSet<>();

        for (Chat chat : chats) {
            chat.getParticipants().forEach(participant -> participantIds.add(participant.getUserId()));
        }

        return participantIds.stream().toList();
    }

    private List<ChatDto> getChatsDto(List<Chat> chats, Map<Long, ParticipantInfo> participantInfos) {

        List<ChatDto> chatDtos = new ArrayList<>();
        for (Chat chat : chats) {
            List<ParticipantInfo> infos = new ArrayList<>();
            for (ChatParticipant participant : chat.getParticipants()) {
                infos.add(participantInfos.get(participant.getUserId()));
            }
            ChatDto curChat = createChatDto(chat, infos);
            chatDtos.add(curChat);
        }
        return chatDtos;
    }

    private ChatDto createChatDto(Chat chat, List<ParticipantInfo> infos) {
        return ChatDto.builder()
                .id(chat.getId())
                .type(chat.getType())
                .title(chat.getTitle())
                .lastActivity(String.valueOf(chat.getUpdatedAt()))
                .unread(false)
                .participantInfo(infos)
                .build();
    }
}
