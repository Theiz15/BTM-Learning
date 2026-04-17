package com.learning.btmlearning.dto.request;

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
public class CertificateAutoIssueRequest {
    @NotNull(message = "User id is required")
    Long userId;

    @NotNull(message = "Course id is required")
    Long courseId;

    @NotNull(message = "Completed lessons is required")
    @Min(value = 0, message = "Completed lessons must be >= 0")
    Integer completedLessons;

    @NotNull(message = "Total lessons is required")
    @Min(value = 1, message = "Total lessons must be >= 1")
    Integer totalLessons;

    @NotNull(message = "Quiz passed is required")
    Boolean quizPassed;
}
