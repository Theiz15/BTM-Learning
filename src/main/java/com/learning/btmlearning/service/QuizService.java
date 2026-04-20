package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.QuizAttemptRequest;
import com.learning.btmlearning.dto.request.QuizRequest;
import com.learning.btmlearning.dto.response.QuizAttemptResponse;
import com.learning.btmlearning.dto.response.QuizResponse;

public interface QuizService {
    QuizResponse createQuiz (QuizRequest request);
    QuizResponse getQuiz (Long quizId);
    void deleteQuiz (Long quizId);
    QuizAttemptResponse submitQuiz(QuizAttemptRequest request);
}
