package com.learning.btmlearning.dto.response;

import com.learning.btmlearning.constant.Provider;
import com.learning.btmlearning.constant.UserRole;
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
public class UserResponse {
    Long id;
    String email;
    String fullName;
    String avatarUrl;
    UserRole role;
    Provider provider;
    Boolean isActive;
    LocalDateTime createdAt;
}
