package com.learning.btmlearning.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PagedCourseResponse {
    List<CourseDetailResponse> content;
    int pageNo;
    int pageSize;
    long totalElements;
    int totalPages;
    boolean last;
}