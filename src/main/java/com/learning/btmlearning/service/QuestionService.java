package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.QuestionRequest;
import com.learning.btmlearning.dto.request.SearchQuestionRequest;
import com.learning.btmlearning.dto.response.QuestionResponse;
import org.springframework.data.domain.Page;

public interface QuestionService {
    QuestionResponse addQuestion (QuestionRequest request);
    QuestionResponse updateQuestion (QuestionRequest request, Long questionId);
    void deleteQuestion (Long questionId);
    Page<QuestionResponse> getQuestions (SearchQuestionRequest request);
}
