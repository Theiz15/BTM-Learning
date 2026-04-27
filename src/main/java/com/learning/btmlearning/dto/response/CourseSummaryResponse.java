package com.learning.btmlearning.dto.response;

import com.learning.btmlearning.constant.CourseStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSummaryResponse {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private String thumbnailUrl;
    private LocalDateTime discountEndDate;
    private String campaignName;
    private BigDecimal originalPrice;
    private BigDecimal price;
    private String level;
    private CourseStatus status;
    private float avgRating;
    private int totalStudents;
    private int totalLessons;
    private LocalDateTime publishDate;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
