package io.github.bigpig.server.repository;

import io.github.bigpig.server.dto.chat.ParticipantInfo;
import io.github.bigpig.server.entity.chat.Chat;
import io.github.bigpig.server.entity.chat.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    @Query("SELECT cp.chat FROM ChatParticipant cp " +
            "WHERE cp.user.username = :username " +
            "AND cp.leftAt IS NULL " +
            "ORDER BY cp.chat.updatedAt DESC")
    List<Chat> findChatsByUsername(@Param("username") String username);

    @Query("""
        SELECT new io.github.bigpig.server.dto.chat.ParticipantInfo(
            cp.user.id, cp.user.nickname, cp.user.username
        )
        FROM ChatParticipant cp
        WHERE cp.chat.id = :chatId
          AND cp.leftAt IS NULL
    """)
    List<ParticipantInfo> findActiveParticipantsWithNicknamesByChatId(@Param("chatId") Long chatId);

    ChatParticipant findChatParticipantByChatIdAndUserId(Long chatId, Long userId);
}
