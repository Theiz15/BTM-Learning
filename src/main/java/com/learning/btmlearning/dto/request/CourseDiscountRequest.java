package com.learning.btmlearning.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class CourseDiscountRequest {
    BigDecimal salePrice;
    LocalDateTime discountEndDate;
    String campaignName ;
}
