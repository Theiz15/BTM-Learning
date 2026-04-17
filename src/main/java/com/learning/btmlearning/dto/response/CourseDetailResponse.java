package com.learning.btmlearning.dto.response;

import com.learning.btmlearning.constant.CourseLevel;
import com.learning.btmlearning.constant.CourseStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseDetailResponse {
    Long id;
    String title;
    String slug;
    Double price;
    CourseLevel level;
    CourseStatus status;
    Double avgRating;
    UserProfile instructor;
    LocalDateTime publishAt ;
    String nameParent ;
//    List<Object> sections;
}