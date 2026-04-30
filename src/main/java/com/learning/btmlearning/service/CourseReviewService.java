package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.CourseReviewCreationRequest;
import com.learning.btmlearning.dto.response.CourseReviewResponse;

import java.util.List;

public interface CourseReviewService {
    CourseReviewResponse createReview(CourseReviewCreationRequest request);

    List<CourseReviewResponse> getReviewsByCourse(Long courseId);
}
