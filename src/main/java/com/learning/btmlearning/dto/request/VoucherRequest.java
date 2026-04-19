package com.learning.btmlearning.dto.request;


import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherRequest {
    @NotBlank()
    String code;

    @Min(1) @Max(100)
    int discountPercent;

    @Min(1)
    int quantity;

    LocalDateTime startDate;
    LocalDateTime expirationDate;
    boolean isActive = true;
}