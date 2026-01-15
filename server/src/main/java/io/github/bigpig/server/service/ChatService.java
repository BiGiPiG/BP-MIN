package io.github.bigpig.server.service;

import io.github.bigpig.server.dto.chat.ChatDto;
import io.github.bigpig.server.dto.chat.CreateChatRequestDto;
import io.github.bigpig.server.dto.chat.ParticipantInfo;
import io.github.bigpig.server.entity.chat.Message;
import io.github.bigpig.server.entity.user.User;
import io.github.bigpig.server.entity.chat.Chat;
import io.github.bigpig.server.entity.chat.ChatParticipant;
import io.github.bigpig.server.entity.chat.ChatType;
import io.github.bigpig.server.event.ChatCreatedEvent;
import io.github.bigpig.server.exceptions.AppException;
import io.github.bigpig.server.exceptions.ErrorCode;
import io.github.bigpig.server.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatParticipantService chatParticipantService;
    private final UserService userService;
    private final ChatRepository chatRepository;
    private final ApplicationEventPublisher eventPublisher;

    public List<Chat> findChatsByUser(UserDetails userDetails) {
        Optional<User> user = userService.findByUsername(userDetails.getUsername());
        return user.map(chatParticipantService::findChatsByUser).orElse(null);
    }

    public Chat findChatById(Long id) {
        return chatRepository.findById(id).orElse(null);
    }

    @Transactional
    public Chat createChat(CreateChatRequestDto requestDto) {
        Chat chat = new Chat(requestDto.type(), requestDto.title());
        for (String username : requestDto.participants()) {
            User participant = userService.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            ChatParticipant chatParticipant = new ChatParticipant(chat, participant);
            chat.addParticipant(chatParticipant);
        }
        Chat newChat = chatRepository.save(chat);
        eventPublisher.publishEvent(new ChatCreatedEvent(this, newChat));
        return chat;
    }

    public ChatDto getChatDto(Chat chat) {
        List<ParticipantInfo> participantInfos = chatParticipantService
                .findActiveParticipantsWithNicknamesByChatId(chat.getId());

        Message lastMessage = chat.getMessages().stream()
                .max(Comparator.comparing(Message::getSentAt))
                .orElse(null);

        String lastMessagePreview = (lastMessage != null) ? lastMessage.getContent() : null;
        LocalDateTime lastActivity = (lastMessage != null) ? lastMessage.getSentAt() : chat.getCreatedAt();
        boolean hasUnread = lastMessage != null;

        return new ChatDto(
                chat.getId(),
                chat.getType(),
                chat.getType() == ChatType.GROUP ? chat.getTitle() : null,
                lastActivity.toString(),
                lastMessagePreview,
                hasUnread,
                participantInfos
        );
    }

}
