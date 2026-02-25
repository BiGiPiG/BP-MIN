package io.github.bigpig.server.service;

import io.github.bigpig.server.dto.chat.PresenceUpdateDto;
import io.github.bigpig.server.entity.chat.Chat;
import io.github.bigpig.server.entity.user.User;
import io.github.bigpig.server.event.PresenceUpdateEvent;
import io.github.bigpig.server.exceptions.AppException;
import io.github.bigpig.server.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PresenceService {
    private static final String ONLINE = "ONLINE";
    private static final String OFFLINE = "OFFLINE";
    private static final String PREFIX = "cache_";


    private final ApplicationEventPublisher eventPublisher;
    private final ChatParticipantService chatParticipantService;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    public String getStatus(Long userId) {
        if (redisTemplate.opsForValue().get(PREFIX + userId) != null) {
            return ONLINE;
        }
        return OFFLINE;
    }

    public void changePresence(Long userId, String status) {
        switch (status) {
            case ONLINE:
                redisTemplate.opsForValue().set(PREFIX + userId, ONLINE);
                break;
            case OFFLINE:
                redisTemplate.delete(PREFIX + userId);
                break;
        }
        broadCastToAllChats(userId, status);
    }

    public void broadCastToAllChats(Long userId, String status) {
        User currentUser = userService.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PresenceUpdateDto presenceUpdateDto = PresenceUpdateDto.builder()
                .userId(currentUser.getId())
                .username(currentUser.getUsername())
                .nickname(currentUser.getNickname())
                .status(status)
                .build();

        for (Chat chat : chatParticipantService.findChatsByUser(currentUser)) {
            eventPublisher.publishEvent(new PresenceUpdateEvent(this, chat.getId(), presenceUpdateDto));
        }
    }
}
