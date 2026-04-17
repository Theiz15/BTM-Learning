package com.learning.btmlearning.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.btmlearning.constant.AiRole;
import com.learning.btmlearning.dto.response.AiChatMessageResponse;
import com.learning.btmlearning.dto.response.AiChatSessionResponse;
import com.learning.btmlearning.dto.response.OpenAiChatResponse;
import com.learning.btmlearning.dto.response.OpenAiChunkResponse;
import com.learning.btmlearning.entity.AiChatSession;
import com.learning.btmlearning.entity.AiMessage;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.entity.User;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.AiChatMessageMapper;
import com.learning.btmlearning.mapper.AiChatSessionMapper;
import com.learning.btmlearning.repository.AiChatSessionRepository;
import com.learning.btmlearning.repository.AiMessageRepository;
import com.learning.btmlearning.repository.CourseRepository;
import com.learning.btmlearning.service.IAiChatService;
import com.learning.btmlearning.utils.SecurityUtil;
import com.learning.btmlearning.utils.SystemPromptBuilder;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class AiChatService implements IAiChatService {
    AiChatSessionRepository sessionRepository;
    AiMessageRepository messageRepository;
    CourseRepository courseRepository;
    OpenAiService openAiService;
    SystemPromptBuilder promptBuilder;
    SecurityUtil securityUtil;
    AiChatMessageMapper aiChatMessageMapper;
    AiChatSessionMapper aiChatSessionMapper;
    ObjectMapper objectMapper;


    @Override
    @Transactional
    public AiChatSessionResponse createSession(Long courseId) {
        User user = securityUtil.getCurrentUser();

        Course course = null;

        if (courseId != null) {
            course = courseRepository.findById(courseId).orElseThrow(
                    () -> new AppException(ErrorCode.COURSE_NOT_FOUND)
            );
        }

        AiChatSession session = AiChatSession.builder()
                .user(user)
                .course(course)
                .sessionToken(UUID.randomUUID().toString())
                .lastActiveAt(LocalDateTime.now())
                .build();

        log.info(">>> Đã tạo Session AI mới: {} cho User: {}", session.getSessionToken(), user.getEmail());
        sessionRepository.save(session) ;
        return aiChatSessionMapper.toResponse(session);
    }

    @Override
    @Transactional
    public AiChatMessageResponse sendMessage(String sessionToken, String userContent) {
        // 2.1 Lấy Session và xác thực quyền sở hữu
        AiChatSession session = getAndVerifySession(sessionToken);

        // 2.2 Lưu tin nhắn của Học viên (USER) vào Database
        AiMessage userMessage = AiMessage.builder()
                .session(session)
                .role(AiRole.USER)
                .content(userContent)
                .tokensUsed(0)
                .build();
        messageRepository.save(userMessage);

        // 2.3 Load 20 tin nhắn gần nhất và đảo ngược danh sách (để AI đọc từ cũ tới mới)
        List<AiMessage> history = messageRepository.findTop20BySessionIdOrderByCreatedAtDesc(session.getId());
        Collections.reverse(history);

        // 2.4 Build Context (Danh sách các tin nhắn để đóng gói gửi đi)
        List<Map<String, String>> contextMessages = new ArrayList<>();

        // Add SYSTEM PROMPT (Luôn nằm ở dòng số 1)
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", promptBuilder.build(session.getCourse()));
        contextMessages.add(systemMsg);

        // Add lịch sử hội thoại
        for (AiMessage msg : history) {
            Map<String, String> historyMsg = new HashMap<>();
            historyMsg.put("role", msg.getRole().name().toLowerCase());
            historyMsg.put("content", msg.getContent());
            contextMessages.add(historyMsg);
        }

        // 2.5 Bắn Request sang OpenAI
        OpenAiChatResponse aiResponse = openAiService.chat(contextMessages);

        // 2.6 Lấy kết quả trả về
        String assistantContent = aiResponse.getChoices().get(0).getMessage().getContent();
        int totalTokens = aiResponse.getUsage().getTotal_tokens();

        // 2.7 Lưu câu trả lời của Trợ lý (ASSISTANT) vào Database
        AiMessage assistantMessage = AiMessage.builder()
                .session(session)
                .role(AiRole.ASSISTANT)
                .content(assistantContent)
                .tokensUsed(totalTokens)
                .build();
        messageRepository.save(assistantMessage);

        session.setLastActiveAt(LocalDateTime.now());
        sessionRepository.save(session);

        return aiChatMessageMapper.toResponse(assistantMessage);
    }

    public List<AiChatMessageResponse> getSessionHistory(String sessionToken) {
        AiChatSession session = getAndVerifySession(sessionToken);

        List<AiMessage> history = messageRepository.findTop20BySessionIdOrderByCreatedAtDesc(session.getId());
        Collections.reverse(history); // Đảo lại theo thứ tự thời gian từ cũ tới mới để vẽ lên màn hình

        return aiChatMessageMapper.toResponseList(history) ;
    }

    @Transactional
    public SseEmitter streamMessage(String sessionToken ,String userContent) {
        AiChatSession session = getAndVerifySession(sessionToken);
        AiMessage userMessage = AiMessage.builder()
                .session(session)
                .role(AiRole.USER)
                .content(userContent)
                .tokensUsed(0)
                .build();

        messageRepository.save(userMessage);

        // Build context
        List<AiMessage> history = messageRepository.findTop20BySessionIdOrderByCreatedAtDesc(session.getId());
        Collections.reverse(history);

        // 2.4 Build Context (Danh sách các tin nhắn để đóng gói gửi đi)
        List<Map<String, String>> contextMessages = new ArrayList<>();

        // Add SYSTEM PROMPT (Luôn nằm ở dòng số 1)
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", promptBuilder.build(session.getCourse()));
        contextMessages.add(systemMsg);

        // Add lịch sử hội thoại
        for (AiMessage msg : history) {
            Map<String, String> historyMsg = new HashMap<>();
            historyMsg.put("role", msg.getRole().name().toLowerCase());
            historyMsg.put("content", msg.getContent());
            contextMessages.add(historyMsg);
        }

        // cbi ống
        SseEmitter emitter = new SseEmitter(120000L);
        StringBuilder fullAssistantResponse = new StringBuilder();

        // 4. Lấy dòng chảy dữ liệu từ OpenAI
        openAiService.streamChat(contextMessages).subscribe(
                chunkString -> {
                    // OpenAI gửi chuỗi kết thúc là "[DONE]"
                    if ("[DONE]".equals(chunkString.trim())) return;

                    try {
                        // Parse chuỗi JSON nhỏ xíu
                        OpenAiChunkResponse chunk = objectMapper.readValue(chunkString, OpenAiChunkResponse.class);
                        if (chunk.getChoices() != null && !chunk.getChoices().isEmpty()) {
                            String contentToken = chunk.getChoices().get(0).getDelta().getContent();
                            if (contentToken != null) {
                                fullAssistantResponse.append(contentToken);
                                emitter.send(SseEmitter.event().data(contentToken));
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Lỗi parse chunk: {}", chunkString);
                    }
                },
                error -> {
                    log.error("Lỗi khi stream AI: ", error);
                    emitter.completeWithError(error);
                },
                () -> {
                    // Lưu toàn bộ câu văn hoàn chỉnh vào Database
                    AiMessage assistantMessage = AiMessage.builder()
                            .session(session)
                            .role(AiRole.ASSISTANT)
                            .content(fullAssistantResponse.toString())
                            // Ở chế độ stream, OpenAI không trả về Token Used, ta ước lượng (1 chữ VN ~ 1.5 token)
                            .tokensUsed((int) (fullAssistantResponse.length() * 1.5 / 4))
                            .build();
                    messageRepository.save(assistantMessage);

                    // Cập nhật session
                    session.setLastActiveAt(LocalDateTime.now());
                    sessionRepository.save(session);

                    try {
                        emitter.send(SseEmitter.event().name("DONE").data("[DONE]"));
                        emitter.complete();
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                }
        );

        return emitter;
    }

    private AiChatSession getAndVerifySession(String sessionToken) {
        AiChatSession session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Phiên chat không tồn tại hoặc đã hết hạn"));

        // Chống Hacker: Chỉ chủ nhân của Session mới được phép lấy lịch sử hoặc nhắn tin
        if (!session.getUser().getId().equals(securityUtil.getCurrentUser().getId())) {
            log.warn(">>> Cảnh báo: User {} đang cố truy cập trái phép vào Session {}", securityUtil.getCurrentUser(), sessionToken);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return session;
    }

}
