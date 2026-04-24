package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.constant.CourseStatus;
import com.learning.btmlearning.dto.response.CourseSummaryResponse;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.mapper.CourseMapper;
import com.learning.btmlearning.repository.CourseRepository;
import com.learning.btmlearning.utils.SystemPromptBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RecommendationService {
    UserLearningProfileBuilder userLearningProfileBuilder;
    CourseRepository courseRepository;
    CourseMapper courseMapper;
    SystemPromptBuilder systemPromptBuilder;

    private static final int TOP_N = 4;

    @Cacheable(value = "course_recommendations", key = "#userId != null ? #userId : 'anonymous'")
    public List<CourseSummaryResponse> getRecommendations(Long userId) {
        // --- Chưa đăng nhập: trả top 4 khóa phổ biến nhất của hệ thống ---
        if (userId == null) {
            log.info(">>> Anonymous user – trả top {} khóa phổ biến nhất", TOP_N);
            List<Course> popularCourses = courseRepository.findTopRatedCourses(
                    CourseStatus.ACTIVE, PageRequest.of(0, TOP_N));
            return popularCourses.stream()
                    .map(courseMapper::toCourseSummaryResponse)
                    .collect(Collectors.toList());
        }

        log.info(">>> Get recommendations for userId={}", userId);
        var profile = userLearningProfileBuilder.buildProfile(userId);
        List<Long> enrolledIds = profile.getEnrolledCourseIds().isEmpty()
                ? null : profile.getEnrolledCourseIds();

        List<Course> recommendedCourses;

        if (profile.getTotalCompleted() < 3) {
            log.info(">>> New user – trả top {} khóa được đánh giá cao", TOP_N);
            recommendedCourses = courseRepository.findTopRatedCourses(
                    CourseStatus.ACTIVE, PageRequest.of(0, TOP_N));
            return recommendedCourses.stream()
                    .map(courseMapper::toCourseSummaryResponse)
                    .collect(Collectors.toList());
        }

        try {
            List<Long> aiSuggestedCategories = systemPromptBuilder.getAiSuggestedCategories(
                    profile.getLearnedCategoryIds());
            log.info(">>> AI gợi ý categories: {}", aiSuggestedCategories);
            recommendedCourses = courseRepository.findRecommendedCourses(
                    aiSuggestedCategories, enrolledIds, PageRequest.of(0, TOP_N));
        } catch (Exception e) {
            log.warn(">>> AI gặp lỗi, fallback content-based. Lỗi: {}", e.getMessage());
            recommendedCourses = courseRepository.findRecommendedCourses(
                    profile.getLearnedCategoryIds(), enrolledIds, PageRequest.of(0, TOP_N));
        }

        return recommendedCourses.stream()
                .map(courseMapper::toCourseSummaryResponse)
                .collect(Collectors.toList());
    }
}
