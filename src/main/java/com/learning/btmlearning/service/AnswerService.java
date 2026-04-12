package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.AnswerRequest;
import com.learning.btmlearning.dto.response.AnswerResponse;

import java.util.List;

public interface AnswerService {
    AnswerResponse createAnswer (AnswerRequest request);
    AnswerResponse updateAnswer (AnswerRequest request, Long answerId);
    void deleteAnswer (Long answerId);
    List<AnswerResponse> getAllAnswers();
}
