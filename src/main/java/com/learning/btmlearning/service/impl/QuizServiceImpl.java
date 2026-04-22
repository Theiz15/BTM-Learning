package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.constant.UserRole;
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
import com.learning.btmlearning.utils.SecurityUtil;
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
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public QuizResponse createQuiz (QuizRequest request) {
        Quiz quiz = quizMapper.toQuiz(request);
        List<QuizRequest.ManualQuestionItem> manualQuestions =
            request.getManualQuestions() == null ? List.of() : request.getManualQuestions();
        List<QuizRequest.RandomQuestionConfig> randomConfigs =
            request.getRandomConfigs() == null ? List.of() : request.getRandomConfigs();

        if (Objects.nonNull(request.getLessonId())) {
            Lesson lesson = lessonRepository.findById(request.getLessonId()).orElseThrow(
                    () -> new AppException(ErrorCode.LESSON_NOT_FOUND)
            );

            assertCanManageCourse(lesson.getCourse());

            quiz.setLesson(lesson);
        }

        Map<Long, QuizQuestion> questionMap = new LinkedHashMap<>();

        // Handle select question
        if (!manualQuestions.isEmpty()) {
            for (QuizRequest.ManualQuestionItem item : manualQuestions) {
                Question question = questionRepository.findById(item.getQuestionId()).orElseThrow(
                        () -> new AppException(ErrorCode.QUESTION_NOT_FOUND)
                );

                questionMap.put(question.getId(), buildQuizQuestion(quiz, question, item.getSortOrder(), item.getScore()));
            }
        }

        // Random question
        if (!randomConfigs.isEmpty()) {
            int autoOrderIndex = questionMap.size();

            for (QuizRequest.RandomQuestionConfig item : randomConfigs) {
                List<Question> randomQuestion = fetchRandomQuestions(item);

                if (randomQuestion.isEmpty()) {
                    throw new AppException(ErrorCode.INVALID_STOCK_QUESTION);
                }

                for (Question question : randomQuestion) {
                    questionMap.putIfAbsent(question.getId(), buildQuizQuestion(quiz, question, autoOrderIndex++, item.getScore()));
                }
            }
        }

        quiz.setQuestions(new ArrayList<>(questionMap.values()));
        quiz.setCreatedAt(LocalDateTime.now());

        int totalScore = questionMap.values().stream().mapToInt(QuizQuestion::getScore).sum();

        QuizResponse quizResponse = quizMapper.toQuizResponse(quizRepository.save(quiz));

        quizResponse.setTotalScore(totalScore);
        quizResponse.setTotalQuestions(questionMap.size());

        return quizResponse;
    }

    @Override
    @Transactional
    public QuizResponse updateQuiz(QuizRequest request, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(
                () -> new AppException(ErrorCode.QUIZ_NOT_FOUND)
        );

        if (quiz.getLesson() != null) {
            assertCanManageCourse(quiz.getLesson().getCourse());
        }

        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setTimeLimitMin(request.getTimeLimitMin());
        quiz.setPassScore(request.getPassScore());
        quiz.setShuffleQuestions(request.isShuffleQuestions());
        quiz.setShuffleAnswers(request.isShuffleAnswers());

        if (Objects.nonNull(request.getLessonId())) {
            Lesson lesson = lessonRepository.findById(request.getLessonId()).orElseThrow(
                    () -> new AppException(ErrorCode.LESSON_NOT_FOUND)
            );
            quiz.setLesson(lesson);
        }

        List<QuizRequest.ManualQuestionItem> manualQuestions =
            request.getManualQuestions() == null ? List.of() : request.getManualQuestions();

        if (!manualQuestions.isEmpty()) {
            // Clear existing quiz questions
            if (quiz.getQuestions() != null) {
                quiz.getQuestions().clear();
                quizRepository.flush();
            }

            Map<Long, QuizQuestion> questionMap = new LinkedHashMap<>();
            for (QuizRequest.ManualQuestionItem item : manualQuestions) {
                Question question = questionRepository.findById(item.getQuestionId()).orElseThrow(
                        () -> new AppException(ErrorCode.QUESTION_NOT_FOUND)
                );
                questionMap.put(question.getId(), buildQuizQuestion(quiz, question, item.getSortOrder(), item.getScore()));
            }
            quiz.setQuestions(new ArrayList<>(questionMap.values()));
        }

        Quiz saved = quizRepository.save(quiz);
        QuizResponse quizResponse = quizMapper.toQuizResponse(saved);

        List<QuizQuestion> quizQuestions = quizQuestionRepository.findAllByQuizIdWithDetails(saved.getId());
        quizResponse.setQuestions(quizQuestions.stream()
                .map(quizMapper::toQuizQuestionResponse)
                .toList());
        quizResponse.setTotalQuestions(quizQuestions.size());
        int totalScore = quizQuestions.stream()
            .map(QuizQuestion::getScore)
            .filter(Objects::nonNull)
            .mapToInt(Integer::intValue)
            .sum();
        quizResponse.setTotalScore(totalScore);

        return quizResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponse getQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(
                () -> new AppException(ErrorCode.QUIZ_NOT_FOUND)
        );

            List<QuizQuestion> quizQuestions = quizQuestionRepository.findAllByQuizIdWithDetails(quizId);

        QuizResponse quizResponse = quizMapper.toQuizResponse(quiz);
        quizResponse.setQuestions(quizQuestions.stream()
                .map(quizMapper::toQuizQuestionResponse)
                .toList());
        
        quizResponse.setTotalQuestions(quizQuestions.size());

        int totalScore = quizQuestions.stream()
            .map(QuizQuestion::getScore)
            .filter(Objects::nonNull)
            .mapToInt(Integer::intValue)
            .sum();
        quizResponse.setTotalScore(totalScore);

        return quizResponse;
    }

    @Override
    @Transactional
    public QuizResponse getQuizByLessonId(Long lessonId) {
        Quiz quiz = quizRepository.findFirstByLessonIdOrderByIdDesc(lessonId)
                .orElseGet(() -> recoverQuizBindingForLesson(lessonId)
                        .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND)));

        return getQuiz(quiz.getId());
    }

    private Optional<Quiz> recoverQuizBindingForLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null) {
            return Optional.empty();
        }

        String lessonTitle = lesson.getTitle() == null ? "" : lesson.getTitle().trim();
        if (lessonTitle.isEmpty()) {
            return Optional.empty();
        }

        Optional<Quiz> orphanQuiz = quizRepository.findFirstByLessonIsNullAndTitleIgnoreCaseOrderByIdDesc(lessonTitle);
        if (orphanQuiz.isEmpty()) {
            return Optional.empty();
        }

        Quiz quiz = orphanQuiz.get();
        quiz.setLesson(lesson);
        return Optional.of(quizRepository.save(quiz));
    }

    @Override
    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(
                () -> new AppException(ErrorCode.QUIZ_NOT_FOUND)
        );

        if (quiz.getLesson() != null) {
            assertCanManageCourse(quiz.getLesson().getCourse());
        }

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
        List<AnswerAttemptRequest> submittedAnswers = request.getAnswers() != null ? request.getAnswers() : new ArrayList<>();
        List<AttemptAnswer> attemptAnswers = mapToAttemptAnswers(
                submittedAnswers, attempt, questionMap, answerMap
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

    @Override
    public List<QuizResponse> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        return quizzes.stream().map(quizMapper::toQuizResponse).toList();
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
        }).collect(Collectors.toList());
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

    private void assertCanManageCourse(Course course) {
        User currentUser = securityUtil.getCurrentUser();
        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }

        // Standalone quiz (not linked to any course) — allow any instructor
        if (course == null) {
            return;
        }

        // Force-initialize lazy proxy to avoid null on lazy-loaded instructor
        User instructor = course.getInstructor();
        Long instructorId = instructor != null ? instructor.getId() : null;
        if (instructorId == null || !Objects.equals(instructorId, currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
