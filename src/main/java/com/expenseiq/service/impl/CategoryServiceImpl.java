package com.expenseiq.service.impl;

import com.expenseiq.dto.request.CategoryRequest;
import com.expenseiq.dto.response.CategoryResponse;
import com.expenseiq.entity.Category;
import com.expenseiq.entity.User;
import com.expenseiq.enums.TransactionType;
import com.expenseiq.exception.BadRequestException;
import com.expenseiq.exception.ResourceNotFoundException;
import com.expenseiq.repository.CategoryRepository;
import com.expenseiq.repository.TransactionRepository;
import com.expenseiq.repository.UserRepository;
import com.expenseiq.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(Long userId, CategoryRequest request) {
        User user = getUserOrThrow(userId);

        Category category = Category.builder()
                .user(user)
                .name(request.getName())
                .type(request.getType())
                .icon(request.getIcon())
                .color(request.getColor())
                .isDefault(false)
                .build();

        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long userId, Long id, CategoryRequest request) {
        Category category = getCategoryOrThrow(id, userId);

        if (category.getIsDefault()) {
            throw new BadRequestException("Cannot update default categories");
        }

        category.setName(request.getName());
        category.setType(request.getType());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());

        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long userId, Long id) {
        Category category = getCategoryOrThrow(id, userId);

        if (category.getIsDefault()) {
            throw new BadRequestException("Cannot delete default categories");
        }

        // Check if category has transactions
        if (transactionRepository.existsByCategoryId(id)) {
            throw new BadRequestException("Cannot delete category with existing transactions");
        }

        categoryRepository.delete(category);
    }

    @Override
    public CategoryResponse getCategoryById(Long userId, Long id) {
        Category category = getCategoryOrThrow(id, userId);
        return mapToResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories(Long userId) {
        List<Category> categories = categoryRepository.findByUserIdOrIsDefaultTrue(userId);
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getCategoriesByType(Long userId, TransactionType type) {
        List<Category> categories = categoryRepository.findByUserIdOrDefaultAndType(userId, type);
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void initializeDefaultCategories() {
        if (categoryRepository.findByIsDefaultTrue().isEmpty()) {
            // Income categories
            createDefaultCategory("Salary", TransactionType.INCOME, "💼", "#10B981");
            createDefaultCategory("Freelance", TransactionType.INCOME, "💰", "#059669");
            createDefaultCategory("Gift", TransactionType.INCOME, "🎁", "#34D399");
            createDefaultCategory("Investment", TransactionType.INCOME, "💵", "#6EE7B7");
            createDefaultCategory("Bonus", TransactionType.INCOME, "🏆", "#A7F3D0");
            createDefaultCategory("Other Income", TransactionType.INCOME, "📈", "#D1FAE5");

            // Expense categories
            createDefaultCategory("Food & Dining", TransactionType.EXPENSE, "🍕", "#EF4444");
            createDefaultCategory("Housing", TransactionType.EXPENSE, "🏠", "#DC2626");
            createDefaultCategory("Transportation", TransactionType.EXPENSE, "🚗", "#B91C1C");
            createDefaultCategory("Groceries", TransactionType.EXPENSE, "🛒", "#991B1B");
            createDefaultCategory("Entertainment", TransactionType.EXPENSE, "🎬", "#F87171");
            createDefaultCategory("Shopping", TransactionType.EXPENSE, "👕", "#FCA5A5");
            createDefaultCategory("Healthcare", TransactionType.EXPENSE, "💊", "#FEE2E2");
            createDefaultCategory("Education", TransactionType.EXPENSE, "📚", "#F59E0B");
            createDefaultCategory("Bills & Utilities", TransactionType.EXPENSE, "💳", "#D97706");
            createDefaultCategory("Travel", TransactionType.EXPENSE, "✈️", "#B45309");
            createDefaultCategory("Personal Care", TransactionType.EXPENSE, "🎉", "#92400E");
            createDefaultCategory("Subscriptions", TransactionType.EXPENSE, "📱", "#78350F");
            createDefaultCategory("Other Expense", TransactionType.EXPENSE, "💰", "#6B7280");
        }
    }

    private void createDefaultCategory(String name, TransactionType type, String icon, String color) {
        Category category = Category.builder()
                .name(name)
                .type(type)
                .icon(icon)
                .color(color)
                .isDefault(true)
                .build();
        categoryRepository.save(category);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Category getCategoryOrThrow(Long id, Long userId) {
        return categoryRepository.findById(id)
                .filter(c -> c.getUser() == null || c.getUser().getId().equals(userId) || c.getIsDefault())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .icon(category.getIcon())
                .color(category.getColor())
                .isDefault(category.getIsDefault())
                .build();
    }
}
