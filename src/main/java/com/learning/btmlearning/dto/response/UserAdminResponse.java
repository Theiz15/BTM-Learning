package com.learning.btmlearning.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAdminResponse {
    Long id;
    String email;
    String fullName;
    String role;
    Boolean isActive;
    LocalDateTime createdAt;
}
