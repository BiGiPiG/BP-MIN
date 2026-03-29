package io.github.bigpig.chatservice.repository;

import io.github.bigpig.chatservice.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
