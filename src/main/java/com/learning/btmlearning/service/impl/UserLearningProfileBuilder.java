package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.repository.EnrollmentRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserLearningProfileBuilder {
    EnrollmentRepository enrollmentRepository;

    @Data
    @Builder
    public static class LearningProfile {
        List<Long> enrolledCourseIds;
        List<Long> learnedCategoryIds;
        int totalCompleted;
    }

    public LearningProfile buildProfile(Long userId) {
        List<Long> enrolledCourseIds = enrollmentRepository.findEnrolledCourseIdsByUserId(userId);
        List<Long> categoryIds = enrollmentRepository.findEnrolledCategoryIdsByUserId(userId);

        return LearningProfile.builder()
                .enrolledCourseIds(enrolledCourseIds)
                .learnedCategoryIds(categoryIds)
                .totalCompleted(enrolledCourseIds.size())
                .build();
    }
}