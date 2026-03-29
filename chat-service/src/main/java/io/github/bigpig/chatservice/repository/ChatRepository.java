package io.github.bigpig.chatservice.repository;

import io.github.bigpig.chatservice.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
