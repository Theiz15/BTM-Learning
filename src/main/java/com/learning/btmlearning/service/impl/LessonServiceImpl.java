package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.dto.request.LessonRequest;
import com.learning.btmlearning.dto.response.LessonResponse;
import com.learning.btmlearning.entity.FileUpload;
import com.learning.btmlearning.entity.Lesson;
import com.learning.btmlearning.entity.Section;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.LessonMapper;
import com.learning.btmlearning.repository.FileUploadRepository;
import com.learning.btmlearning.repository.LessonRepository;
import com.learning.btmlearning.repository.QuizRepository;
import com.learning.btmlearning.repository.SectionRepository;
import com.learning.btmlearning.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final FileUploadRepository fileUploadRepository;
    private final SectionRepository sectionRepository;
    private final QuizRepository quizRepository;

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
    public LessonResponse updateLesson(LessonRequest request) {
        return null;
    }

    @Override
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new AppException(ErrorCode.LESSON_NOT_FOUND)
        );

        lessonRepository.delete(lesson);
    }
}
