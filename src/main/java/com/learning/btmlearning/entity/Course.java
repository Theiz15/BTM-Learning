package com.learning.btmlearning.entity;

import com.learning.btmlearning.constant.CourseStatus;
import com.learning.btmlearning.constant.CourseLevel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Course extends BaseEntity {
    String title ;

    String slug ;

    String description ;

    String thumbnailUrl ;

    long price ;

    @Enumerated(EnumType.STRING)
    CourseLevel level ;

    @Enumerated(EnumType.STRING)
    CourseStatus status ;

    @Builder.Default
    float avgRating = 0.0F;

    int totalStudents ;

    int totalLessons ;

    LocalDateTime publishAt ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    User instructor ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

//    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Builder.Default
//    List<Section> sections = new ArrayList<>();
}
