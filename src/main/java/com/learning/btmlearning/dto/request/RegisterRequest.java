package com.learning.btmlearning.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @NotBlank(message = "Email cannot blank")
    @Email
    String email;

    @NotBlank
    @Size(min = 8, message = "password at least 8 characters")
    String password;

    @NotBlank
    @Size(min = 2, max = 150)
    String fullName;
}
