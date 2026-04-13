package com.learning.btmlearning.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@ConfigurationProperties(prefix = "openai")
@Data
@EnableRetry
public class OpenAiConfig {
    private String apiUrl;
    private String apiKey;
    private String model;
    private int maxTokens;
    private double temperature;
}