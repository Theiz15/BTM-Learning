package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.CategoryCreationRequest;
import com.learning.btmlearning.dto.request.CategoryUpdateRequest;
import com.learning.btmlearning.dto.response.CategoryResponse;
import com.learning.btmlearning.entity.Category;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryCreationRequest request);

    List<CategoryResponse> getCategories() ;

    CategoryResponse getCategoryById(Long categoryId);

    CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest request);

    void deleteCategory(Long categoryId) ;

    Category findCategory(Long categoryId);
}
