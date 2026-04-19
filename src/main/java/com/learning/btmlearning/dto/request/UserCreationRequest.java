package com.learning.btmlearning.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class UserCreationRequest {
    @NotBlank(message = "EMAIL_IS_REQUIRED")
    @Email(message = "EMAIL_IS_INVALID")
    String email;

    @NotBlank(message = "FULL_NAME_IS_REQUIRED")
    String fullName;

    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;
}
