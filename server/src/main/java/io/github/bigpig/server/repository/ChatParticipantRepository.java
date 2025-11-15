package io.github.bigpig.server.repository;

import io.github.bigpig.server.dto.ParticipantInfo;
import io.github.bigpig.server.entity.Chat;
import io.github.bigpig.server.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    @Query("SELECT cp.chat FROM ChatParticipant cp " +
            "WHERE cp.user = :user " +
            "AND cp.leftAt IS NULL " +
            "ORDER BY cp.chat.updatedAt DESC")
    List<Chat> findChatsByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT new io.github.bigpig.server.dto.ParticipantInfo(
            cp.user.id, cp.user.username
        )
        FROM ChatParticipant cp
        WHERE cp.chat.id = :chatId
          AND cp.leftAt IS NULL
    """)
    List<ParticipantInfo> findActiveParticipantsWithNicknamesByChatId(@Param("chatId") Long chatId);
}
