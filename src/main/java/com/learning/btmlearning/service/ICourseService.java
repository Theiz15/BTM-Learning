package com.learning.btmlearning.service;

import com.learning.btmlearning.constant.CourseLevel;
import com.learning.btmlearning.dto.request.CreateCourseRequest;
import com.learning.btmlearning.dto.response.CourseDetailResponse;
import com.learning.btmlearning.dto.response.PagedCourseResponse;

public interface ICourseService {
    CourseDetailResponse createCourse(CreateCourseRequest createCourseRequest);
    PagedCourseResponse getPublicCourses(String keyword, CourseLevel level, String sortBy, int page, int size) ;
    void submitCourse(Long courseId) ;

}
