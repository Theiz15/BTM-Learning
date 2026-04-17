package com.learning.btmlearning.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {
    Long id;
    String title;
    String slug;
    String description;
    String thumbnailUrl;
    Integer categoryId;
    String categoryName;
    Long instructorId;
    Double averageRating;
    Integer ratingCount;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
