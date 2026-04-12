package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.dto.request.AnswerRequest;
import com.learning.btmlearning.dto.response.AnswerResponse;
import com.learning.btmlearning.entity.Answer;
import com.learning.btmlearning.entity.Question;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.AnswerMapper;
import com.learning.btmlearning.repository.AnswerRepository;
import com.learning.btmlearning.repository.QuestionRepository;
import com.learning.btmlearning.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final AnswerMapper answerMapper;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public AnswerResponse createAnswer(AnswerRequest request) {
        Answer answer = answerMapper.toAnswer(request);

        if (Objects.nonNull(request.getQuestionId())) {
            Question question = questionRepository.findById(request.getQuestionId()).orElseThrow(
                    () -> new AppException(ErrorCode.QUESTION_NOT_FOUND)
            );

            answer.setQuestion(question);
        }

        return answerMapper.toAnswerResponse(answerRepository.save(answer));
    }

    @Override
    public AnswerResponse updateAnswer(AnswerRequest request, Long answerId) {
        Answer answer = answerRepository.findById(answerId).orElseThrow(
                () -> new AppException(ErrorCode.ANSWER_NOT_FOUND)
        );

        if (Objects.nonNull(request.getQuestionId())) {
            Question question = questionRepository.findById(request.getQuestionId()).orElseThrow(
                    () -> new AppException(ErrorCode.QUESTION_NOT_FOUND)
            );

            answer.setQuestion(question);
        }

        return answerMapper.toAnswerResponse(answerRepository.save(answer));
    }

    @Override
    public void deleteAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId).orElseThrow(
                () -> new AppException(ErrorCode.ANSWER_NOT_FOUND)
        );

        answerRepository.delete(answer);
    }

    @Override
    public List<AnswerResponse> getAllAnswers() {
        List<Answer> answers = answerRepository.findAll();

        return answers.stream().map(answerMapper::toAnswerResponse).collect(Collectors.toList());
    }
}
