package com.learning.btmlearning.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfile {
    Long id;
    String email;
    String fullName;
    String avatarUrl;
    String role;
    String bio;
    boolean isActive;
    LocalDateTime createdAt;
}