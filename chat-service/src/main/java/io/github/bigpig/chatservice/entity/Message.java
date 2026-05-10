package io.github.bigpig.chatservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private ChatParticipant chatParticipant;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "is_read")
    private Boolean isRead;

    public Message(Chat chat, ChatParticipant chatParticipant, String content) {
        this.chat = chat;
        this.chatParticipant = chatParticipant;
        this.content = content;
        this.sentAt = LocalDateTime.now();
        this.isRead = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) && Objects.equals(chat, message.chat)
                && Objects.equals(chatParticipant, message.chatParticipant) && Objects.equals(content, message.content)
                && Objects.equals(sentAt, message.sentAt) && Objects.equals(isRead, message.isRead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chat, chatParticipant, content, sentAt, isRead);
    }

    public void read() {
        this.isRead = true;
    }
}