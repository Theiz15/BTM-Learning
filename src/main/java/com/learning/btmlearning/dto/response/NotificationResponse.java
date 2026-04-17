package com.learning.btmlearning.dto.response;

import com.learning.btmlearning.constant.NotificationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    Long id;
    Long userId;
    String title;
    String message;
    NotificationType type;
    Boolean isRead;
    LocalDateTime createdAt;
}
