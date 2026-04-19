package com.learning.btmlearning.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherResponse {
    Long id;
    String code;
    Integer discountPercent;
    Integer quantity;
    Integer usedCount;
    LocalDateTime startDate;
    LocalDateTime expirationDate;
    Boolean isActive;
}
