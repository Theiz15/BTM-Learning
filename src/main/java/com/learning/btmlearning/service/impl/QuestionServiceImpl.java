package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.constant.QuestionType;
import com.learning.btmlearning.dto.request.AnswerRequest;
import com.learning.btmlearning.dto.request.QuestionRequest;
import com.learning.btmlearning.dto.request.SearchQuestionRequest;
import com.learning.btmlearning.dto.response.QuestionResponse;
import com.learning.btmlearning.entity.Answer;
import com.learning.btmlearning.entity.Question;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.AnswerMapper;
import com.learning.btmlearning.mapper.QuestionMapper;
import com.learning.btmlearning.repository.QuestionRepository;
import com.learning.btmlearning.repository.QuizRepository;
import com.learning.btmlearning.service.QuestionService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionMapper questionMapper;
    private final QuestionRepository questionRepository;
    private final AnswerMapper answerMapper;
    private final QuizRepository quizRepository;

    @Override
    public QuestionResponse addQuestion(QuestionRequest request) {
        Question question = questionMapper.toQuestion(request);

        syncAnswer(question, request.getAnswers());

        return questionMapper.toQuestionResponse(questionRepository.save(question));
    }

    @Override
    public QuestionResponse updateQuestion(QuestionRequest request, Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new AppException(ErrorCode.QUESTION_NOT_FOUND)
        );

        questionMapper.updateQuestion(question, request);
        syncAnswer(question, request.getAnswers());

        return questionMapper.toQuestionResponse(questionRepository.save(question));
    }

    @Override
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new AppException(ErrorCode.QUESTION_NOT_FOUND)
        );

        questionRepository.delete(question);
    }

    @Override
    public Page<QuestionResponse> getQuestions(SearchQuestionRequest request) {
        Specification<Question> spec = buildSpec(request);
        Pageable pageable = PageRequest.of(request.getPageNo(), request.getPageSize());

        Page<Question> questions = questionRepository.findAll(spec, pageable);

        return questions.map(questionMapper::toQuestionResponse);
    }

    @Override
    public List<QuestionResponse> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId)
                .stream()
                .map(questionMapper::toQuestionResponse)
                .toList();
    }

    @Override
    public QuestionResponse assignQuestionToQuiz(Long questionId, Long quizId) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new AppException(ErrorCode.QUESTION_NOT_FOUND)
        );

        if (quizId == null) {
            // Unassign: detach from current quiz
            question.setQuiz(null);
        } else {
            var quiz = quizRepository.findById(quizId).orElseThrow(
                    () -> new AppException(ErrorCode.QUIZ_NOT_FOUND)
            );
            question.setQuiz(quiz);
        }

        return questionMapper.toQuestionResponse(questionRepository.save(question));
    }

    private void syncAnswer (Question question, List<AnswerRequest> requests) {
        if (requests == null || requests.isEmpty()) return;

        validateAnswers(question.getQuestionType(), requests);

        // Clear all answers before set new answers
        if (question.getAnswers() != null) {
            question.getAnswers().clear();
        }

        for (AnswerRequest answerRequest : requests) {
            Answer answer = answerMapper.toAnswer(answerRequest);
            answer.setQuestion(question);
            answer.setReferenceAnswer(answerRequest.getReferenceAnswer());
            question.getAnswers().add(answer);
        }
    }

    private void validateAnswers (QuestionType type, List<AnswerRequest> requests) {
        if (type == QuestionType.ESSAY || type == QuestionType.SHORT_ANSWER) return;

        long correctCount = requests.stream()
                .filter(AnswerRequest::isCorrect)
                .count();

        boolean checkType = type == QuestionType.SINGLE_CHOICE || type == QuestionType.TRUE_FALSE;

        if (checkType && (correctCount != 1))   {
            throw new AppException(ErrorCode.INVALID_SINGLE_ANSWER);
        }

        if (type == QuestionType.MULTIPLE_CHOICE && correctCount <= 1) {
            throw new AppException(ErrorCode.INVALID_SINGLE_ANSWER);
        }
    }

    private Specification<Question> buildSpec (SearchQuestionRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(request.getKeyword())) {
                predicates.add(cb.like(cb.lower(root.get("content")), "%" + request.getKeyword().toLowerCase() + "%"));
            }

            if (Objects.nonNull(request.getDifficulty())) {
                predicates.add(cb.equal(root.get("difficulty"), request.getDifficulty()));
            }

            if (Boolean.TRUE.equals(request.getUnassigned())) {
                predicates.add(cb.isNull(root.get("quiz")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
