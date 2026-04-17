package com.learning.btmlearning.dto.request;

import com.learning.btmlearning.constant.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeRoleRequest {
    @NotNull(message = "Role cannot blank")
    UserRole role;
}
