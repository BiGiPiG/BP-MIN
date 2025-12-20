package io.github.bigpig.server.repository;

import io.github.bigpig.server.entity.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Chat.ChatMessage, Long> {
    List<Chat.ChatMessage> findByChatId(Long chatId);
}
