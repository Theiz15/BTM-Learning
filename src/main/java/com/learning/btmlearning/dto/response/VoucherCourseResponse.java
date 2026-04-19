package com.learning.btmlearning.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherCourseResponse {
    String code ;
    Integer discountPercent;
    BigDecimal finalPrice;
    String message ;
}
