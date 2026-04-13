package com.learning.btmlearning.dto.response;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AiChatSessionResponse {
    String sessionToken;
    LocalDateTime lastActiveAt;
}