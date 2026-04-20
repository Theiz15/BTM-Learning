package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.LessonRequest;
import com.learning.btmlearning.dto.request.LessonUpdateRequest;
import com.learning.btmlearning.dto.request.UpdateProgressRequest;
import com.learning.btmlearning.dto.response.CourseProgressResponse;
import com.learning.btmlearning.dto.response.LessonProgressResponse;
import com.learning.btmlearning.dto.response.LessonResponse;

public interface LessonService {
    LessonResponse createLesson (LessonRequest request);
    LessonResponse updateLesson (LessonUpdateRequest request, Long lessonId);
    void deleteLesson (Long lessonId);

    // Lesson tracking
    LessonProgressResponse getOrCreate(Long lessonId);
    LessonProgressResponse updateProgress(UpdateProgressRequest request);
    LessonResponse get(Long lessonId);

    // Tracking course
    CourseProgressResponse getCourseProgress(Long courseId);
}
