package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.constant.EnrollmentStatus;
import com.learning.btmlearning.constant.ProgressStatus;
import com.learning.btmlearning.dto.request.LessonRequest;
import com.learning.btmlearning.dto.request.LessonUpdateRequest;
import com.learning.btmlearning.dto.request.UpdateProgressRequest;
import com.learning.btmlearning.dto.response.CourseProgressResponse;
import com.learning.btmlearning.dto.response.LessonProgressResponse;
import com.learning.btmlearning.dto.response.LessonResponse;
import com.learning.btmlearning.entity.*;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.LessonMapper;
import com.learning.btmlearning.mapper.LessonProgressMapper;
import com.learning.btmlearning.repository.*;
import com.learning.btmlearning.service.LessonService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final FileUploadRepository fileUploadRepository;
    private final SectionRepository sectionRepository;
    private final QuizRepository quizRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final UserRepository userRepository;
    private final LessonProgressMapper lessonProgressMapper;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public LessonResponse createLesson(LessonRequest request) {
        Lesson lesson = lessonMapper.toLesson(request);

        FileUpload fileUpload = fileUploadRepository.findById(request.getFileUploadId()).orElseThrow(
                () -> new AppException(ErrorCode.FILE_NOT_FOUND)
        );

        switch (request.getLessonType()) {
            case DOCUMENT -> lesson.setDocumentUrl(fileUpload.getFilePath());
            case VIDEO -> lesson.setVideoUrl(fileUpload.getFilePath());
            case QUIZ -> lesson.setQuiz(quizRepository.findById(request.getQuizId()).orElseThrow(
                    () -> new AppException(ErrorCode.QUIZ_NOT_FOUND)
            ));
            default -> throw new IllegalStateException("Unexpected value: " + request.getLessonType());
        }

        Section section = sectionRepository.findById(request.getSectionId()).orElseThrow(
                () -> new AppException(ErrorCode.SECTION_NOT_FOUND)
        );

        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setSection(section);

        return lessonMapper.toLessonResponse(lessonRepository.save(lesson));
    }

    @Override
    public LessonResponse updateLesson(LessonUpdateRequest request) {
        return null;
    }

    @Override
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new AppException(ErrorCode.LESSON_NOT_FOUND)
        );

        lessonRepository.delete(lesson);
    }

    @Transactional
    @Override
    public LessonProgressResponse getOrCreate(Long userId, Long lessonId) {
        LessonProgress lessonProgress = lessonProgressRepository
                .findByUserIdAndLessonId(userId, lessonId)
                .orElseGet(() -> {
                    Lesson lesson = lessonRepository.getReferenceById(lessonId);

                    LessonProgress progress = new LessonProgress();
                    progress.setUser((com.learning.btmlearning.entity.User) userRepository.findById(userId).orElse(null));
                    progress.setLesson(lesson);
                    progress.setEnrollment(findEnrollment(userId, lesson.getCourse().getId()));
                    progress.setWatchedSeconds(0);
                    progress.setIsCompleted(false);
                    return progress;
                });

        return lessonProgressMapper.toLessonProgressResponse(lessonProgressRepository.save(lessonProgress));
    }

    @Transactional
    @Override
    public LessonProgressResponse updateProgress(Long userId,
                                                 UpdateProgressRequest request) {
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Lesson not found: " + request.getLessonId()));

        Long courseId = lesson.getSection().getCourse().getId();

        Enrollment enrollment = enrollmentRepository
                .findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new IllegalStateException(
                        "User chưa đăng ký khoá học này"));

        // Upsert: lấy record cũ hoặc tạo mới
        LessonProgress progress = lessonProgressRepository
                .findByEnrollmentIdAndLessonId(enrollment.getId(), lesson.getId())
                .orElseGet(() -> {
                    LessonProgress p = new LessonProgress();
                    p.setEnrollment(enrollment);
                    p.setLesson(lesson);
                    return p;
                });

        // Cập nhật theo loại lesson
        switch (lesson.getLessonType()) {
            case VIDEO   -> updateVideoProgress(progress, request, lesson);
            case DOCUMENT -> updateReadingProgress(progress, request);
            case QUIZ    -> updateQuizProgress(progress, request);
        }

        progress.setWatchedSeconds(
                    progress.getWatchedSeconds() + request.getWatchedSeconds());

        progress.setLastWatchedAt(LocalDateTime.now());

        // Đánh dấu completed nếu chưa có
        if (progress.getIsCompleted()
                && progress.getLastWatchedAt() == null) {
            progress.setCompleteAt(LocalDateTime.now());
        }

        LessonProgress saved = lessonProgressRepository.save(progress);

        checkAndCompleteCourse(enrollment);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public CourseProgressResponse getCourseProgress(Long userId, Long courseId) {
        Enrollment enrollment = enrollmentRepository
                .findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new IllegalStateException("User not join this course"));

        // Map lessonId → progress for lookup O(1)
        Map<Long, LessonProgress> progressMap = lessonProgressRepository
                .findByEnrollmentId(enrollment.getId())
                .stream()
                .collect(Collectors.toMap(p -> p.getLesson().getId(), p -> p));

        Course course = enrollment.getCourse();
        int totalLessons = 0;
        int completedLessons = 0;

        List<CourseProgressResponse.SectionProgressResponse> sectionResponses = new ArrayList<>();

        for (Section section : course.getSections()) {
            int sectionTotal = 0;
            int sectionCompleted = 0;
            List<LessonProgressResponse> lessonResponses = new ArrayList<>();

            for (Lesson lesson : section.getLessons()) {
                totalLessons++;
                sectionTotal++;

                LessonProgress p = progressMap.get(lesson.getId());
                if (p != null && p.getIsCompleted()) {
                    completedLessons++;
                    sectionCompleted++;
                }

                lessonResponses.add(p != null ? toResponse(p) : emptyResponse(lesson));
            }

            sectionResponses.add(CourseProgressResponse.SectionProgressResponse.builder()
                    .sectionId(section.getId())
                    .sectionTitle(section.getTitle())
                    .totalLessons(sectionTotal)
                    .completedLessons(sectionCompleted)
                    .lessons(lessonResponses)
                    .build());
        }

        int totalTime = lessonProgressRepository.sumTimeSpentByEnrollment(enrollment.getId());
        int percent = totalLessons > 0 ? (completedLessons * 100 / totalLessons) : 0;

        return CourseProgressResponse.builder()
                .courseId(courseId)
                .courseTitle(course.getTitle())
                .totalLessons(totalLessons)
                .completedLessons(completedLessons)
                .progressPercent(percent)
                .totalTimeSpentSeconds(totalTime)
                .sections(sectionResponses)
                .build();
    }

    private void updateVideoProgress(LessonProgress p, UpdateProgressRequest req, Lesson lesson) {
        if (req.getWatchedSeconds() == 0) return;

        if (req.getWatchedSeconds() > p.getWatchedSeconds()) {
            p.setWatchedSeconds(req.getWatchedSeconds());
        }

        int watched = Math.min(req.getWatchedSeconds(), lesson.getDurationSeconds());

        // If watch >= 90 % is auto complete
        if (watched >= lesson.getDurationSeconds() * 0.9) {
            p.setIsCompleted(true);
        }

    }

    private void updateReadingProgress(LessonProgress p, UpdateProgressRequest req) {
        // Reading is completed when user call API (frontend )
        if (p.getIsCompleted()) {
            p.setStatus(ProgressStatus.COMPLETED);
        }
    }

    private void updateQuizProgress(LessonProgress p, UpdateProgressRequest req) {
        if (req.getQuizScore() == null) return;

        p.setQuizScore(req.getQuizScore());

        if (p.getStatus() != ProgressStatus.COMPLETED) {
            p.setStatus(ProgressStatus.COMPLETED);
        }
    }

    private void checkAndCompleteCourse(Enrollment enrollment) {
        // count lesson in course
        int totalLessons = enrollment.getCourse().getSections().stream()
                .mapToInt(s -> s.getLessons().size())
                .sum();

        int completedLessons = lessonProgressRepository
                .countCompletedByEnrollment(enrollment.getId());

        if (totalLessons > 0 && completedLessons >= totalLessons) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollmentRepository.save(enrollment);
        }
    }

    private LessonProgressResponse toResponse(LessonProgress p) {
        return LessonProgressResponse.builder()
                .lessonId(p.getLesson().getId())
                .lessonTitle(p.getLesson().getTitle())
                .lessonType(p.getLesson().getLessonType().name())
                .status(p.getStatus())
                .timeSpentSeconds(p.getWatchedSeconds())
                .quizScore(p.getQuizScore())
                .lastAccessedAt(p.getLastWatchedAt())
                .completedAt(p.getCompleteAt())
                .build();
    }

    private LessonProgressResponse emptyResponse(Lesson lesson) {
        return LessonProgressResponse.builder()
                .lessonId(lesson.getId())
                .lessonTitle(lesson.getTitle())
                .lessonType(lesson.getLessonType().name())
                .status(ProgressStatus.NOT_STARTED)
                .watchPercent(0)
                .timeSpentSeconds(0)
                .build();
    }

    private Enrollment findEnrollment(Long userId, Long courseId) {
        return enrollmentRepository.findByUserIdAndCourseId(userId, courseId).orElseThrow(
                () -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND)
        );
    }
}
