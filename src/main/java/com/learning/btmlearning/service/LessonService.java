package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.LessonRequest;
import com.learning.btmlearning.dto.response.LessonResponse;

public interface LessonService {
    LessonResponse createLesson (LessonRequest request);
    LessonResponse updateLesson (LessonRequest request);
    void deleteLesson (Long lessonId);
}
