package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.constant.CourseStatus;
import com.learning.btmlearning.dto.request.CourseDiscountRequest;
import com.learning.btmlearning.dto.request.CourseRequest;
import com.learning.btmlearning.dto.response.CourseResponse;
import com.learning.btmlearning.dto.response.CourseSummaryResponse;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.entity.CoursePromotion;
import com.learning.btmlearning.entity.FileUpload;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.CourseMapper;
import com.learning.btmlearning.repository.CoursePromotionRepository;
import com.learning.btmlearning.repository.CourseRepository;
import com.learning.btmlearning.repository.FileUploadRepository;
import com.learning.btmlearning.service.CourseService;
import com.learning.btmlearning.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final FileUploadRepository fileUploadRepository;
    private final SecurityUtil securityUtil;
    private final CoursePromotionRepository coursePromotionRepository;

    @Override
    public CourseResponse createCourse(CourseRequest request) {
        Course course = courseMapper.toCourse(request);

        if (Objects.nonNull(request.getFileUploadId())) {
            FileUpload fileUpload = fileUploadRepository.findById(request.getFileUploadId()).orElseThrow(
                    () -> new AppException(ErrorCode.FILE_NOT_FOUND)
            );

            course.setThumbnailUrl(fileUpload.getFilePath());
        }

        course.setCreateAt(LocalDateTime.now());
        course.setStatus(CourseStatus.DRAFT);
        course.setOriginalPrice(request.getPrice());
        return courseMapper.toCourseResponse(courseRepository.save(course));
    }

    @Override
    public CourseResponse updateCourse(CourseRequest request, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );

        courseMapper.updateCourse(course, request);
        course.setUpdateAt(LocalDateTime.now());

        if (request.getStatus() == CourseStatus.PUBLISHED) {
            course.setPublishDate(LocalDateTime.now());
        }

        return courseMapper.toCourseResponse(courseRepository.save(course));
    }

    @Override
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );

        course.setStatus(CourseStatus.INACTIVE);
        courseRepository.save(course);
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseRepository.findAll();

        return courses.stream().map(courseMapper::toCourseResponse).collect(Collectors.toList());
    }

    @Override
    public CourseResponse getCourseById(Long courseId) {
        Course course = getCourseAndValidateStatus(courseId) ;

        return courseMapper.toCourseResponse(course);
    }

    @Override
    public Course findCourse(Long courseId) {
        return courseRepository.findById(courseId).orElseThrow(
                () -> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );
    }

    @Override
    public void updateCourseRating(Long courseId, double rating, long count) {
        Course course = findCourse(courseId);
        course.setAvgRating((float) rating);
        courseRepository.save(course);
    }
    public Page<CourseResponse> getPendingCourses(Pageable pageable) {
        Page<Course> pendingCourses = courseRepository.findByStatus(CourseStatus.PENDING ,pageable) ;

        return pendingCourses.map(courseMapper::toCourseResponse);
    }

    @Override
    public void approveCourse(Long courseId) {
        Course course = getCourseAndValidateStatus(courseId) ;

        course.setStatus(CourseStatus.ACTIVE);
        course.setPublishDate(LocalDateTime.now());
        courseRepository.save(course);
    }

    @Override
    public void rejectCourse(Long courseId) {
        Course course =  getCourseAndValidateStatus(courseId) ;

        course.setStatus(CourseStatus.DRAFT);
        courseRepository.save(course);
    }

    @Override
    public CourseSummaryResponse updateCourseDiscount(Long courseId, CourseDiscountRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        CoursePromotion promotion = CoursePromotion.builder()
                .course(course)
                .campaignName(request.getCampaignName())
                .salePrice(request.getSalePrice())
                .startDate(LocalDateTime.now())
                .endDate(request.getDiscountEndDate())
                .createdBy(securityUtil.getCurrentUser().getEmail())
                .build();
        coursePromotionRepository.save(promotion);

        if (request.getSalePrice().compareTo(course.getOriginalPrice()) >= 0) {
            course.setPrice(course.getOriginalPrice());
            course.setDiscountEndDate(null);
        } else {
            course.setPrice(request.getSalePrice());
            course.setDiscountEndDate(request.getDiscountEndDate());
        }

        return courseMapper.toCourseSummaryResponse(courseRepository.save(course));
    }

    private Course getCourseAndValidateStatus(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getStatus().equals(CourseStatus.PENDING)) {
            throw new RuntimeException("Khóa học không ở trạng thái chờ duyệt (PENDING)");
        }
        return course;
    }
}
