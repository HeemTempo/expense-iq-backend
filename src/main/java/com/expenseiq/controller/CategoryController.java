package com.expenseiq.controller;

import com.expenseiq.dto.request.CategoryRequest;
import com.expenseiq.dto.response.ApiResponse;
import com.expenseiq.dto.response.CategoryResponse;
import com.expenseiq.enums.TransactionType;
import com.expenseiq.security.SecurityUser;
import com.expenseiq.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @AuthenticationPrincipal SecurityUser currentUser,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id) {
        categoryService.deleteCategory(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id) {
        CategoryResponse response = categoryService.getCategoryById(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(
            @AuthenticationPrincipal SecurityUser currentUser,
            @RequestParam(required = false) TransactionType type) {
        List<CategoryResponse> categories;
        if (type != null) {
            categories = categoryService.getCategoriesByType(currentUser.getId(), type);
        } else {
            categories = categoryService.getAllCategories(currentUser.getId());
        }
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }
}
