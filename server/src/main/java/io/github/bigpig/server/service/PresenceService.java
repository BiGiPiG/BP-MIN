package io.github.bigpig.server.service;

import io.github.bigpig.server.dto.chat.PresenceUpdateDto;
import io.github.bigpig.server.entity.chat.Chat;
import io.github.bigpig.server.entity.user.User;
import io.github.bigpig.server.event.PresenceUpdateEvent;
import io.github.bigpig.server.exceptions.AppException;
import io.github.bigpig.server.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PresenceService {
    private final static int CHECK_INTERVAL = 10000;
    private final static int INACTIVE_INTERVAL = 60000;


    private final ApplicationEventPublisher eventPublisher;
    private final ChatParticipantService chatParticipantService;
    private final UserService userService;

    private final Map<Long, Long> userLastActivity = new ConcurrentHashMap<>();


    public void updatePresence(Long userId) {
        long now = System.currentTimeMillis();

        if (!userLastActivity.containsKey(userId)) {
            broadCastToAllChats(userId, "ONLINE");
        }
        userLastActivity.put(userId, now);
    }

    @Scheduled(fixedRate = CHECK_INTERVAL)
    public void checkLastActivity() {
        for (Long userId : userLastActivity.keySet()) {
            Long lastActivity = userLastActivity.get(userId);
            System.out.println(userId);
            if (System.currentTimeMillis() - lastActivity > INACTIVE_INTERVAL) {
                broadCastToAllChats(userId, "OFFLINE");
                userLastActivity.remove(userId);
            } else {
                broadCastToAllChats(userId, "ONLINE");
            }
        }
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
