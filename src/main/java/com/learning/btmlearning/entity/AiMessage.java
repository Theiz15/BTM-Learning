package com.learning.btmlearning.entity;

import com.learning.btmlearning.constant.AiRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AiMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    AiChatSession session;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AiRole role;

    @Column(columnDefinition = "TEXT", nullable = false)
    String content;

    @Column(name = "tokens_used")
    Integer tokensUsed;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;
}
