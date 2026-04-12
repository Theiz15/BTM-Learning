package com.learning.btmlearning.dto.request;

import lombok.Data;

@Data
public class SectionRequest {
    private Long courseId;
    private String title;
    private int orderIndex;
}
