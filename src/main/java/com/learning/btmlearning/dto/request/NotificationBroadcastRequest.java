package com.learning.btmlearning.dto.request;

import com.learning.btmlearning.constant.NotificationType;
import com.learning.btmlearning.constant.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationBroadcastRequest {
    UserRole role;

    @NotBlank(message = "Title is required")
    String title;

    @NotBlank(message = "Message is required")
    String message;

    @NotNull(message = "Notification type is required")
    NotificationType type;
}
