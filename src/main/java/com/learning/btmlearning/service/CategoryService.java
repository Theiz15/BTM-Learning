package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.CategoryCreationRequest;
import com.learning.btmlearning.dto.request.CategoryUpdateRequest;
import com.learning.btmlearning.dto.response.CategoryResponse;
import com.learning.btmlearning.entity.Category;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    private static final Pattern NON_LATIN = Pattern.compile("[^a-z0-9-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");

    CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse createCategory(CategoryCreationRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTS);
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(generateUniqueSlug(request.getName(), null));
        category.setDescription(request.getDescription());
        category.setIsActive(true);

        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = findCategory(categoryId);
        return mapToResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest request) {
        Category category = findCategory(categoryId);

        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTS);
        }

        category.setName(request.getName());
        category.setSlug(generateUniqueSlug(request.getName(), category.getId()));
        category.setDescription(request.getDescription());
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }

        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = findCategory(categoryId);
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    private String generateUniqueSlug(String name, Long categoryId) {
        String baseSlug = toSlug(name);
        String slug = baseSlug;
        int suffix = 1;

        while (isSlugTaken(slug, categoryId)) {
            slug = baseSlug + "-" + suffix++;
        }

        return slug;
    }

    private boolean isSlugTaken(String slug, Long categoryId) {
        if (categoryId == null) {
            return categoryRepository.existsBySlug(slug);
        }
        return categoryRepository.existsBySlugAndIdNot(slug, categoryId);
    }

    private String toSlug(String input) {
        String safe = input == null ? "" : input;
        String nowhitespace = WHITESPACE.matcher(safe.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{M}+", "");
        String slug = withoutDiacritics.toLowerCase(Locale.ROOT);
        slug = NON_LATIN.matcher(slug).replaceAll("");
        slug = slug.replaceAll("-+", "-");
        slug = slug.replaceAll("^-+|-+$", "");
        return slug.isBlank() ? "category" : slug;
    }
}
