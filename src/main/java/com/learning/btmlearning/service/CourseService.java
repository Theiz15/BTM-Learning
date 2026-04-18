package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.CourseRequest;
import com.learning.btmlearning.dto.response.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    CourseResponse createCourse (CourseRequest request);
    CourseResponse updateCourse (CourseRequest request, Long courseId);
    void deleteCourse (Long courseId);
    List<CourseResponse> getAllCourses();
    CourseResponse getCourseById(Long courseId);

    Page<CourseResponse> getPendingCourses(Pageable pageable);
    void approveCourse (Long courseId);
    void rejectCourse (Long courseId);
}
