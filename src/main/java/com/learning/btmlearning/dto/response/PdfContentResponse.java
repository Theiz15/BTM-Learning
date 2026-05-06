package com.learning.btmlearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfContentResponse {
    private String fileName;
    private int totalPages;
    private String author;
    private String title;
    private String subject;
    private String creator;

    private String fullText;

    private Map<Integer, String> pageContents;
}