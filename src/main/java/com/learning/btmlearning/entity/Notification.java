package com.learning.btmlearning.entity;

import com.learning.btmlearning.constant.NotificationType;
import com.learning.btmlearning.constant.NotificationTypeConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false)
    String title;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    String message;

    @Column(nullable = false)
    @Convert(converter = NotificationTypeConverter.class)
    NotificationType type;

    Boolean isRead = false;

    @CreationTimestamp
    LocalDateTime createdAt;
}
