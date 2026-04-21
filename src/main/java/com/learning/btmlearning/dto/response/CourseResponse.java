package com.learning.btmlearning.dto.response;

import com.learning.btmlearning.constant.CourseStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseResponse {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private String thumbnailUrl;
    private BigDecimal originalPrice;
    private BigDecimal price;
    private LocalDateTime discountEndDate;
    private String campaignName;
    private String level;
    private CourseStatus status;
    private float avgRating;
    private int totalStudents;
    private int totalLessons;
    private int reviewCount;
    private LocalDateTime publishDate;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    private List<SectionResponse> sections;
    private CategoryResponse category;
    private UserProfile instructor;
}
