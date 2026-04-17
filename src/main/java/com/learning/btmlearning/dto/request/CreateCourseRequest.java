package com.learning.btmlearning.dto.request;

import com.learning.btmlearning.constant.CourseLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCourseRequest {
    @NotBlank
    String title ;

    @NotBlank
    String slug ;

    @DecimalMin("0")
    BigDecimal price ;

    CourseLevel level ;

    @NotNull()
    Long categoryId;

}
