package com.learning.btmlearning.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OpenAiChunkResponse {
    List<Choice> choices;

    @Data
    public static class Choice {
        private Delta delta;
    }

    @Data
    public static class Delta {
        private String content; // Từng chữ rớt xuống ở đây
    }
}