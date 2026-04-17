package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.LessonRequest;
import com.learning.btmlearning.dto.request.LessonUpdateRequest;
import com.learning.btmlearning.dto.request.UpdateProgressRequest;
import com.learning.btmlearning.dto.response.CourseProgressResponse;
import com.learning.btmlearning.dto.response.LessonProgressResponse;
import com.learning.btmlearning.dto.response.LessonResponse;

public interface LessonService {
    LessonResponse createLesson (LessonRequest request);
    LessonResponse updateLesson (LessonUpdateRequest request);
    void deleteLesson (Long lessonId);

    // Lesson tracking
    LessonProgressResponse getOrCreate(Long userId, Long lessonId);
    LessonProgressResponse updateProgress(Long userId, UpdateProgressRequest request);

    // Tracking course
    CourseProgressResponse getCourseProgress(Long userId, Long courseId);
}
