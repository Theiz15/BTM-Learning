package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.configuration.OpenAiConfig;
import com.learning.btmlearning.dto.response.OpenAiChatResponse;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OpenAiService {
    OpenAiConfig openAiConfig;
    RestTemplate restTemplate = new RestTemplate();

    @Retryable(
            retryFor = { HttpClientErrorException.TooManyRequests.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public OpenAiChatResponse chat(List<Map<String,String>> messages){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiConfig.getApiKey());
        
        Map<String ,Object> requestBody = new HashMap<>();
        requestBody.put("model",openAiConfig.getModel());
        requestBody.put("messages",messages);
        requestBody.put("max_tokens",openAiConfig.getMaxTokens());
        requestBody.put("temperature",openAiConfig.getTemperature());

        HttpEntity<Map<String ,Object>> request = new HttpEntity<>(requestBody, headers);
        try {
            log.info(">>> Đang gửi yêu cầu tới AI Model: {}", openAiConfig.getModel());
            return restTemplate.postForObject(
                    openAiConfig.getApiUrl(),
                    request,
                    OpenAiChatResponse.class
            );
        } catch (Exception e) {
            log.error(">>> Lỗi khi gọi API OpenAI/Groq", e);
            throw new RuntimeException("Không thể kết nối tới AI Server lúc này.");
        }
    }

    public Flux<String> streamChat(List<Map<String, String>> messages) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", openAiConfig.getModel());
        requestBody.put("messages", messages);
        requestBody.put("temperature", openAiConfig.getTemperature());

        requestBody.put("stream", true);

        log.info(">>> Đang mở kết nối STREAM tới AI Model...");

        return WebClient.builder().build().post()
                .uri(openAiConfig.getApiUrl())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiConfig.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class);
    }

    @Recover
    public OpenAiChatResponse recover(HttpClientErrorException.TooManyRequests e) {
        log.error(">>> Đã thử lại nhiều lần nhưng AI vẫn quá tải.");
        throw new AppException(ErrorCode.AI_SERVER_OVERLOADED);
    }

}
