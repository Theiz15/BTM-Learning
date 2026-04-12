package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.dto.request.QuizRequest;
import com.learning.btmlearning.dto.response.QuizResponse;
import com.learning.btmlearning.entity.Lesson;
import com.learning.btmlearning.entity.Question;
import com.learning.btmlearning.entity.Quiz;
import com.learning.btmlearning.entity.QuizQuestion;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.QuizMapper;
import com.learning.btmlearning.repository.LessonRepository;
import com.learning.btmlearning.repository.QuestionRepository;
import com.learning.btmlearning.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl {
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;
    private final QuestionRepository questionRepository;
    private final LessonRepository lessonRepository;

    public QuizResponse createQuiz (QuizRequest request) {
        Quiz quiz = quizMapper.toQuiz(request);

        if (Objects.nonNull(request.getLessonId())) {
            Lesson lesson = lessonRepository.findById(request.getLessonId()).orElseThrow(
                    () -> new AppException(ErrorCode.LESSON_NOT_FOUND)
            );

            quiz.setLesson(lesson);
        }

        Map<Long, QuizQuestion> questionMap = new LinkedHashMap<>();

        // Handle select question
        if (!request.getManualQuestions().isEmpty()) {
            for (QuizRequest.ManualQuestionItem item : request.getManualQuestions()) {
                Question question = questionRepository.findById(item.getQuestionId()).orElseThrow(
                        () -> new AppException(ErrorCode.QUESTION_NOT_FOUND)
                );

                questionMap.put(question.getId(), buildQuizQuestion(quiz, question, item.getSortOrder(), item.getScore()));
            }
        }

        // Random question

        if (!request.getRandomConfigs().isEmpty()) {
            int autoOrderIndex = questionMap.size();

            for (QuizRequest.RandomQuestionConfig item : request.getRandomConfigs()) {
                List<Question> randomQuestion = fetchRandomQuestions(item);

                if (randomQuestion.isEmpty()) {
                    throw new AppException(ErrorCode.INVALID_STOCK_QUESTION);
                }

                for (Question question : randomQuestion) {
                    questionMap.putIfAbsent(question.getId(), buildQuizQuestion(quiz, question, autoOrderIndex++, item.getScore()));
                }
            }
        }

        if (!questionMap.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_STOCK_QUESTION);
        }

        quiz.setQuestions(new ArrayList<>(questionMap.values()));
        quiz.setCreatedAt(LocalDateTime.now());

        int totalScore = questionMap.values().stream().mapToInt(QuizQuestion::getScore).sum();

        QuizResponse quizResponse = quizMapper.toQuizResponse(quizRepository.save(quiz));

        quizResponse.setTotalScore(totalScore);

        return quizResponse;
    }

    private List<Question> fetchRandomQuestions(QuizRequest.RandomQuestionConfig config) {
        if (Objects.nonNull(config.getDifficulty())) {
            return questionRepository.findRandomByDifficulty(config.getDifficulty(), config.getAmount());
        }

        return questionRepository.findRandom(config.getAmount());
    }

    private QuizQuestion buildQuizQuestion (Quiz quiz, Question question, int orderIndex, int score) {
        QuizQuestion quizQuestion = new QuizQuestion();
        quizQuestion.setQuestion(question);
        quizQuestion.setQuiz(quiz);
        quizQuestion.setScore(score);
        quizQuestion.setSortOrder(orderIndex);

        return quizQuestion;
    }
}
