package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.dto.request.AnswerAttemptRequest;
import com.learning.btmlearning.dto.request.QuizAttemptRequest;
import com.learning.btmlearning.dto.request.QuizRequest;
import com.learning.btmlearning.dto.response.QuizAttemptResponse;
import com.learning.btmlearning.dto.response.QuizResponse;
import com.learning.btmlearning.entity.*;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.QuizMapper;
import com.learning.btmlearning.repository.*;
import com.learning.btmlearning.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;
    private final QuestionRepository questionRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final AnswerRepository answerRepository;

    @Override
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

    @Override
    public QuizResponse getQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(
                () -> new AppException(ErrorCode.QUIZ_NOT_FOUND)
        );

        return quizMapper.toQuizResponse(quiz);
    }

    @Override
    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(
                () -> new AppException(ErrorCode.QUIZ_NOT_FOUND)
        );

        quizRepository.delete(quiz);
    }

    @Transactional
    public QuizAttemptResponse submitQuiz(Long userId, QuizAttemptRequest request) {

        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        QuizAttempt attempt = QuizAttempt.builder()
                .user((User) userRepository.findById(userId).orElse(null))
                .quiz(quiz)
                .startedAt(LocalDateTime.now())
                .build();

        quizAttemptRepository.save(attempt);

        // 2. load questions + answers
        List<QuizQuestion> quizQuestions = quizQuestionRepository.findAllByQuizIdWithDetails(quiz.getId());
        List<Question> questions = quizQuestions.stream().map(QuizQuestion::getQuestion).toList();

        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        List<Answer> answers = answerRepository.findByQuestionIn(questions);
        Map<Long, Answer> answerMap = answers.stream()
                .collect(Collectors.toMap(Answer::getId, a -> a));

        // 3. map answer
        List<AttemptAnswer> attemptAnswers = mapToAttemptAnswers(
                request.getAnswers(), attempt, questionMap, answerMap
        );

        attempt.setAttemptAnswers(attemptAnswers);

        // 4. Scoring
        int correct = 0;

        for (AttemptAnswer aa : attemptAnswers) {
            if (aa.getSelectedAnswer() != null) {
                boolean isCorrect = aa.getSelectedAnswer().isCorrect();
                aa.setIsCorrect(isCorrect);
                if (isCorrect) correct++;
            }
        }

        attempt.setScore(correct);
        attempt.setTotalQuestions(attemptAnswers.size());
        attempt.setIsPassed(correct >= quiz.getPassScore());
        attempt.setSubmittedAt(LocalDateTime.now());

        quizAttemptRepository.save(attempt);

        // 5. response
        return QuizAttemptResponse.builder()
                .score(correct)
                .totalQuestions(attemptAnswers.size())
                .isPassed(attempt.getIsPassed())
                .build();
    }

    private List<AttemptAnswer> mapToAttemptAnswers(
            List<AnswerAttemptRequest> requests,
            QuizAttempt attempt,
            Map<Long, Question> questionMap,
            Map<Long, Answer> answerMap
    ) {
        return requests.stream().map(req -> {
            Question question = questionMap.get(req.getQuestionId());

            AttemptAnswer aa = AttemptAnswer.builder()
                    .attempt(attempt)
                    .question(question)
                    .build();

            if (req.getSelectedAnswerId() != null) {
                aa.setSelectedAnswer(answerMap.get(req.getSelectedAnswerId()));
            }

            aa.setEssayAnswer(req.getEssayAnswer());

            return aa;
        }).toList();
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
