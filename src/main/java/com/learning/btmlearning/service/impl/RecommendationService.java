package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.constant.CourseStatus;
import com.learning.btmlearning.dto.response.CourseSummaryResponse;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.mapper.CourseMapper;
import com.learning.btmlearning.repository.CourseRepository;
import com.learning.btmlearning.utils.SystemPromptBuilder;
import com.learning.btmlearning.utils.UserLearningProfileBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RecommendationService {
    UserLearningProfileBuilder userLearningProfileBuilder;
    CourseRepository courseRepository;
    CourseMapper courseMapper;
    SystemPromptBuilder systemPromptBuilder;

    @Cacheable(value = "course_recommendations", key = "#userId")
    public List<CourseSummaryResponse> getRecommendations(Long userId) {
        log.info("Get recommendations for user " + userId);

        var profile = userLearningProfileBuilder.buildProfile(userId);
        List<Long> enrolledIds = profile.getEnrolledCourseIds().isEmpty() ? null : profile.getEnrolledCourseIds();

        List<Course> recommendedCourses ;

        if (profile.getTotalCompleted() < 3){
            log.info("new user");

            recommendedCourses = courseRepository.findTopRatedCourses(CourseStatus.ACTIVE , PageRequest.of(0, 10));

            return recommendedCourses.stream().map(courseMapper::toCourseSummaryResponse).collect(java.util.stream.Collectors.toList());
        }

        //Call AI and handle FallBack
        try{
            List<Long> aiSuggestedCategories = systemPromptBuilder.getAiSuggestedCategories(profile.getLearnedCategoryIds());
            log.info(">>> AI gợi ý các Category: {}", aiSuggestedCategories);

            recommendedCourses = courseRepository.findRecommendedCourses(
                    aiSuggestedCategories, enrolledIds, PageRequest.of(0, 10));

        } catch (Exception e) {
            log.warn(">>> AI gặp lỗi, chuyển sang Fallback Content-Based. Lỗi: {}", e.getMessage());
            recommendedCourses = courseRepository.findRecommendedCourses(
                    profile.getLearnedCategoryIds(), enrolledIds, PageRequest.of(0, 10));
        }

        return recommendedCourses.stream().map(courseMapper::toCourseSummaryResponse).collect(java.util.stream.Collectors.toList());
    }
}
