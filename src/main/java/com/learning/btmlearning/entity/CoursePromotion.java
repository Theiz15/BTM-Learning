package com.learning.btmlearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_promotions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoursePromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private String campaignName;
    private BigDecimal salePrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String createdBy;
}