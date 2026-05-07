package com.learning.btmlearning.entity;

import com.learning.btmlearning.constant.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "courses")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String slug;
    private String description;
    private String thumbnailUrl;
    private BigDecimal originalPrice;
    private BigDecimal price;
    private String level;

    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    private float avgRating;
    private int totalStudents;
    private int totalLessons;
    private int reviewCount = 0;
    private LocalDateTime publishDate;

    @CreationTimestamp
    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    private LocalDateTime discountEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public BigDecimal getActualPrice() {
        BigDecimal safeOriginalPrice = this.originalPrice == null ? BigDecimal.ZERO : this.originalPrice;
        BigDecimal safePrice = this.price == null ? BigDecimal.ZERO : this.price;

        boolean hasOriginalPrice = safeOriginalPrice.compareTo(BigDecimal.ZERO) > 0;
        boolean hasPrice = safePrice.compareTo(BigDecimal.ZERO) > 0;

        if (!hasOriginalPrice && !hasPrice) {
            return BigDecimal.ZERO;
        }

        if (hasPrice && (!hasOriginalPrice || this.discountEndDate == null || this.discountEndDate.isAfter(LocalDateTime.now()))) {
            return safePrice;
        }

        if (hasOriginalPrice) {
            return safeOriginalPrice;
        }

        return safePrice;
    }
}
