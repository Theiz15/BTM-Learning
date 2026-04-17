package com.learning.btmlearning.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CourseCreationRequest {
    @NotBlank(message = "Course title is required")
    String title;
    String description;

    @NotNull(message = "Category id is required")
    Integer categoryId;

    @NotNull(message = "Instructor id is required")
    Long instructorId;
}
