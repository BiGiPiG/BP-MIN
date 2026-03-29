package io.github.bigpig.chatservice.repository;

import io.github.bigpig.chatservice.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    List<ChatParticipant> findByUserId(long userId);
    ChatParticipant findByChatIdAndUserId(long chatId, long userId);
}
