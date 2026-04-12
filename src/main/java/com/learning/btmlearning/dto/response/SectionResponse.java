package com.learning.btmlearning.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class SectionResponse {
    private Long id;
    private String title;
    private int orderIndex;
    private List<LessonResponse> lessons;
}
