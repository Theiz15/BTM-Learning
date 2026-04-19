package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.request.CategoryCreationRequest;
import com.learning.btmlearning.dto.response.CategoryResponse;
import com.learning.btmlearning.entity.Category;
import org.mapstruct.Mapper;

@Mapper (
        componentModel = "spring"
)
public interface CategoryMapper {
    Category toCategory (CategoryCreationRequest request);
    CategoryResponse toCategoryResponse (Category category);
}
