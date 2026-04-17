package com.learning.btmlearning.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class CourseReviewCreationRequest {
    @NotNull(message = "Course id is required")
    Long courseId;

    @NotNull(message = "User id is required")
    Long userId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be from 1 to 5")
    @Max(value = 5, message = "Rating must be from 1 to 5")
    Integer rating;

    String comment;
}
