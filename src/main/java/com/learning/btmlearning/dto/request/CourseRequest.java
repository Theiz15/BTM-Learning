package com.learning.btmlearning.dto.request;

import com.learning.btmlearning.constant.CourseStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CourseRequest {
    private String title;
    private String slug;
    private String description;
    private BigDecimal price;
    private CourseStatus status;
    private String level;
    private float avgRating;
    private int totalStudents;
    private LocalDateTime publishDate;
    private Long fileUploadId;
    private Long categoryId;
}
