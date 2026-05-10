package io.github.bigpig.chatservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "chat_participants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"chat_id", "user_id"}))
public class ChatParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    public ChatParticipant(Chat chat, Long userId) {
        this.chat = chat;
        this.userId = userId;
        this.joinedAt = LocalDateTime.now();
        this.leftAt = null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChatParticipant that = (ChatParticipant) o;
        return Objects.equals(id, that.id) && Objects.equals(chat, that.chat) && Objects.equals(userId, that.userId) && Objects.equals(joinedAt, that.joinedAt) && Objects.equals(leftAt, that.leftAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chat, userId, joinedAt, leftAt);
    }
}
