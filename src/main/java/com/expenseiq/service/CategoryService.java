package com.expenseiq.service;

import com.expenseiq.dto.request.CategoryRequest;
import com.expenseiq.dto.response.CategoryResponse;
import com.expenseiq.enums.TransactionType;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(Long userId, CategoryRequest request);
    CategoryResponse updateCategory(Long userId, Long id, CategoryRequest request);
    void deleteCategory(Long userId, Long id);
    CategoryResponse getCategoryById(Long userId, Long id);
    List<CategoryResponse> getAllCategories(Long userId);
    List<CategoryResponse> getCategoriesByType(Long userId, TransactionType type);
    void initializeDefaultCategories();
}
