package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.QuizAttemptRequest;
import com.learning.btmlearning.dto.request.QuizRequest;
import com.learning.btmlearning.dto.response.QuizAttemptResponse;
import com.learning.btmlearning.dto.response.QuizResponse;

import java.util.List;

public interface QuizService {
    QuizResponse createQuiz (QuizRequest request);
    QuizResponse updateQuiz (QuizRequest request, Long quizId);
    QuizResponse getQuiz (Long quizId);
    QuizResponse getQuizByLessonId(Long lessonId);
    void deleteQuiz (Long quizId);
    QuizAttemptResponse submitQuiz(QuizAttemptRequest request);
    List<QuizResponse> getAllQuizzes();
}
