package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.constant.CourseLevel;
import com.learning.btmlearning.constant.CourseStatus;
import com.learning.btmlearning.dto.request.CreateCourseRequest;
import com.learning.btmlearning.dto.response.CourseDetailResponse;
import com.learning.btmlearning.dto.response.PagedCourseResponse;
import com.learning.btmlearning.entity.Category;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.entity.User;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.CategoryRepository;
import com.learning.btmlearning.mapper.CourseMapper;
import com.learning.btmlearning.repository.CourseRepository;
import com.learning.btmlearning.repository.UserRepository;
import com.learning.btmlearning.service.ICourseService;
import com.learning.btmlearning.utils.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CourseServiceImpl implements ICourseService {
    CourseRepository courseRepository;
    CourseMapper courseMapper;
    CategoryRepository categoryRepository;
    SecurityUtil securityUtil;

    @Override
    public CourseDetailResponse createCourse(CreateCourseRequest rq) {
        Course course =courseMapper.toCourse(rq) ;

        courseRepository.existsBySlug(rq.getSlug()).orElseThrow(() -> new AppException(ErrorCode.SLUG_REALLY_EXIST)) ;

        Category category = categoryRepository.findById(rq.getCategoryId()).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)
        );

        course.setCategory(category);
        course.setInstructor(securityUtil.getCurrentUser());
        course.setStatus(CourseStatus.DRAFT);
        courseRepository.save(course);

        return courseMapper.toCourseDetailResponse(course);
    }


    @Override
    public PagedCourseResponse getPublicCourses(String keyword, CourseLevel level, String sortBy, int page, int size) {
        Sort sort = Sort.unsorted();
        if ("popular".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "avgRating");
        } else if ("newest".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        } else if ("price_asc".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "price");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Course> coursePage = courseRepository.searchPublicCourses(keyword, level, pageable);

        List<CourseDetailResponse> content = coursePage.getContent().stream()
                .map(courseMapper::toCourseDetailResponse)
                .toList();
        return PagedCourseResponse.builder()
                .content(content)
                .pageNo(coursePage.getNumber())
                .pageSize(coursePage.getSize())
                .totalElements(coursePage.getTotalElements())
                .totalPages(coursePage.getTotalPages())
                .last(coursePage.isLast())
                .build();
    }

    @Override
    public void submitCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new AppException(ErrorCode.COURSE_NOT_FOUND)
        ) ;

        if (!course.getInstructor().getId().equals(securityUtil.getCurrentUser().getId())) {
            throw new AppException(ErrorCode.YOU_ARE_NOT_INSTRUCTOR);
        }

        course.setStatus(CourseStatus.PENDING);
        courseRepository.save(course);
    }
}
