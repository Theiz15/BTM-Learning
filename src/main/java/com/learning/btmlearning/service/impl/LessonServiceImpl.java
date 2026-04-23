package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.constant.EnrollmentStatus;
import com.learning.btmlearning.constant.LessonType;
import com.learning.btmlearning.constant.ProgressStatus;
import com.learning.btmlearning.constant.UserRole;
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
import com.learning.btmlearning.utils.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final CourseRepository courseRepository;
    private final SecurityUtil securityUtil;

    @Transactional
    @Override
    public LessonResponse createLesson(LessonRequest request) {
        Lesson lesson = lessonMapper.toLesson(request);

        Section section = sectionRepository.findById(request.getSectionId()).orElseThrow(
                () -> new AppException(ErrorCode.SECTION_NOT_FOUND)
        );

        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(
                () -> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );

        assertCanManageCourse(course);

        if (section.getCourse() == null || !Objects.equals(section.getCourse().getId(), course.getId())) {
            throw new AppException(ErrorCode.SECTION_NOT_FOUND);
        }

        // Collect quiz IDs (support both single quizId and list quizIds)
        List<Long> quizIdList = new java.util.ArrayList<>();
        if (request.getQuizIds() != null && !request.getQuizIds().isEmpty()) {
            quizIdList.addAll(request.getQuizIds());
        } else if (request.getQuizId() != null) {
            quizIdList.add(request.getQuizId());
        }

        bindLessonResources(lesson, request.getLessonType(), request.getFileUploadId(), quizIdList);

        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setSection(section);
        lesson.setCourse(course);

        return lessonMapper.toLessonResponse(lessonRepository.save(lesson));
    }

    @Transactional
    @Override
    public LessonResponse updateLesson(LessonUpdateRequest request, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new AppException(ErrorCode.LESSON_NOT_FOUND)
        );

        assertCanManageCourse(lesson.getCourse());

        lessonMapper.updateLesson(lesson, request);

        if (request.getSectionId() != null && (lesson.getSection() == null || !Objects.equals(lesson.getSection().getId(), request.getSectionId()))) {
            Section section = sectionRepository.findById(request.getSectionId()).orElseThrow(
                    () -> new AppException(ErrorCode.SECTION_NOT_FOUND)
            );

            assertCanManageCourse(section.getCourse());
            lesson.setSection(section);
            lesson.setCourse(section.getCourse());
        }

        // Only rebind resources when explicitly provided (avoid clearing quizzes when just updating title)
        boolean hasFileUpdate = request.getFileUploadId() != null;
        boolean hasQuizUpdate = request.getQuizIds() != null || request.getQuizId() != null;

        if (hasFileUpdate || hasQuizUpdate) {
            List<Long> quizIdList = new java.util.ArrayList<>();
            if (request.getQuizIds() != null && !request.getQuizIds().isEmpty()) {
                quizIdList.addAll(request.getQuizIds());
            } else if (request.getQuizId() != null) {
                quizIdList.add(request.getQuizId());
            }
            bindLessonResources(lesson, lesson.getLessonType(), request.getFileUploadId(), quizIdList);
        }

        return lessonMapper.toLessonResponse(lessonRepository.save(lesson));
    }

    @Transactional
    @Override
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new AppException(ErrorCode.LESSON_NOT_FOUND)
        );

        assertCanManageCourse(lesson.getCourse());

        // Detach quizzes from this lesson before deleting (keep quizzes intact)
        if (lesson.getQuizzes() != null) {
            for (Quiz quiz : lesson.getQuizzes()) {
                quiz.setLesson(null);
            }
            quizRepository.saveAll(lesson.getQuizzes());
            lesson.getQuizzes().clear();
        }

        lessonRepository.delete(lesson);
    }

    @Transactional
    @Override
    public LessonProgressResponse getOrCreate(Long lessonId) {
        User user = securityUtil.getCurrentUser();

        LessonProgress lessonProgress = lessonProgressRepository
                .findByUserIdAndLessonId(user.getId(), lessonId)
                .orElseGet(() -> {
                    Lesson lesson = lessonRepository.getReferenceById(lessonId);

                    LessonProgress progress = new LessonProgress();
                    progress.setUser((com.learning.btmlearning.entity.User) userRepository.findById(user.getId()).orElse(null));
                    progress.setLesson(lesson);
                    progress.setEnrollment(findEnrollment(user.getId(), lesson.getCourse().getId()));
                    progress.setWatchedSeconds(0);
                    progress.setIsCompleted(false);
                    return progress;
                });

        return lessonProgressMapper.toLessonProgressResponse(lessonProgressRepository.save(lessonProgress));
    }

    @Transactional
    @Override
    public LessonProgressResponse updateProgress(UpdateProgressRequest request) {
        User user = securityUtil.getCurrentUser();

        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Lesson not found: " + request.getLessonId()));

        Long courseId = lesson.getSection().getCourse().getId();

        Enrollment enrollment = enrollmentRepository
                .findByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new IllegalStateException(
                        "User chưa đăng ký khoá học này"));

        // Upsert: lấy record cũ hoặc tạo mới
        LessonProgress progress = lessonProgressRepository
                .findByEnrollmentIdAndLessonId(enrollment.getId(), lesson.getId())
                .orElseGet(() -> {
                    LessonProgress p = new LessonProgress();
                    p.setUser(user);
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

        progress.setLastWatchedAt(LocalDateTime.now());

        if (Boolean.TRUE.equals(progress.getIsCompleted()) && progress.getCompleteAt() == null) {
            progress.setCompleteAt(LocalDateTime.now());
        }

        LessonProgress saved = lessonProgressRepository.save(progress);

        checkAndCompleteCourse(enrollment);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public CourseProgressResponse getCourseProgress(Long courseId) {
        User user = securityUtil.getCurrentUser();

        Enrollment enrollment = enrollmentRepository
                .findByUserIdAndCourseId(user.getId(), courseId)
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
        if (req.getWatchedSeconds() <= 0) return;

        int currentWatchedSeconds = p.getWatchedSeconds() == null ? 0 : p.getWatchedSeconds();
        int watchedSeconds = Math.max(currentWatchedSeconds, req.getWatchedSeconds());
        p.setWatchedSeconds(watchedSeconds);
        p.setStatus(ProgressStatus.IN_PROGRESS);

        int lessonDurationSeconds = Math.max(lesson.getDurationSeconds(), 1);
        int completeThreshold = Math.max((int) Math.ceil(lessonDurationSeconds * 0.9), 1);

        if (watchedSeconds >= completeThreshold) {
            p.setStatus(ProgressStatus.COMPLETED);
            p.setIsCompleted(true);
        }
    }

    private void updateReadingProgress(LessonProgress p, UpdateProgressRequest req) {
        p.setStatus(ProgressStatus.COMPLETED);
        p.setIsCompleted(true);
    }

    private void updateQuizProgress(LessonProgress p, UpdateProgressRequest req) {
        if (req.getQuizScore() == null) return;

        p.setQuizScore(req.getQuizScore());
        p.setStatus(ProgressStatus.COMPLETED);
        p.setIsCompleted(true);
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
            enrollment.setCompletedAt(LocalDateTime.now());
            enrollment.setProgressPercent(100f);
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

    private void bindLessonResources(Lesson lesson, LessonType lessonType, Long fileUploadId, List<Long> quizIds) {
        switch (lessonType) {
            case DOCUMENT -> {
                // Document: require file, optionally bind quizzes too
                if (fileUploadId != null) {
                    FileUpload fileUpload = fileUploadRepository.findById(fileUploadId).orElseThrow(
                            () -> new AppException(ErrorCode.FILE_NOT_FOUND)
                    );
                    lesson.setDocumentUrl(fileUpload.getFilePath());
                }
                lesson.setVideoUrl(null);
                // Bind quizzes if provided
                bindQuizzes(lesson, quizIds);
            }
            case VIDEO -> {
                if (fileUploadId != null) {
                    FileUpload fileUpload = fileUploadRepository.findById(fileUploadId).orElseThrow(
                            () -> new AppException(ErrorCode.FILE_NOT_FOUND)
                    );
                    lesson.setVideoUrl(fileUpload.getFilePath());
                }
                lesson.setDocumentUrl(null);
                // Bind quizzes if provided
                bindQuizzes(lesson, quizIds);
            }
            case QUIZ -> {
                // Quiz-only lesson
                lesson.setDocumentUrl(null);
                lesson.setVideoUrl(null);
                bindQuizzes(lesson, quizIds);
            }
            default -> throw new IllegalStateException("Unexpected value: " + lessonType);
        }
    }

    private void bindQuizzes(Lesson lesson, List<Long> quizIds) {
        clearLessonQuizLinks(lesson);

        if (quizIds == null || quizIds.isEmpty()) return;

        for (Long quizId : quizIds) {
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));

            // Detach from previous lesson if needed
            Lesson previousLesson = quiz.getLesson();
            if (previousLesson != null
                    && !Objects.equals(previousLesson.getId(), lesson.getId())
                    && previousLesson.getQuizzes() != null) {
                previousLesson.getQuizzes().removeIf(q -> Objects.equals(q.getId(), quiz.getId()));
            }

            quiz.setLesson(lesson);
            ensureLessonQuizList(lesson).add(quiz);
        }
    }

    private List<Quiz> ensureLessonQuizList(Lesson lesson) {
        if (lesson.getQuizzes() == null) {
            lesson.setQuizzes(new ArrayList<>());
        }
        return lesson.getQuizzes();
    }

    private void clearLessonQuizLinks(Lesson lesson) {
        List<Quiz> quizzes = ensureLessonQuizList(lesson);
        for (Quiz existingQuiz : quizzes) {
            if (existingQuiz != null && existingQuiz.getLesson() != null) {
                existingQuiz.setLesson(null);
            }
        }
        quizzes.clear();
    }

    private void assertCanManageCourse(Course course) {
        User currentUser = securityUtil.getCurrentUser();
        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }

        if (course == null || course.getInstructor() == null || !Objects.equals(course.getInstructor().getId(), currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
