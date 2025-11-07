package com.expenseiq.service.impl;

import com.expenseiq.dto.request.BudgetRequest;
import com.expenseiq.dto.response.BudgetResponse;
import com.expenseiq.dto.response.CategoryResponse;
import com.expenseiq.entity.Budget;
import com.expenseiq.entity.Category;
import com.expenseiq.entity.User;
import com.expenseiq.enums.TransactionType;
import com.expenseiq.exception.DuplicateResourceException;
import com.expenseiq.exception.ResourceNotFoundException;
import com.expenseiq.repository.BudgetRepository;
import com.expenseiq.repository.CategoryRepository;
import com.expenseiq.repository.TransactionRepository;
import com.expenseiq.repository.UserRepository;
import com.expenseiq.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public BudgetResponse createBudget(Long userId, BudgetRequest request) {
        User user = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(request.getCategoryId());

        // Check if budget already exists for this category and month
        if (budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                userId, request.getCategoryId(), request.getMonth(), request.getYear()).isPresent()) {
            throw new DuplicateResourceException("Budget already exists for this category and month");
        }

        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .amount(request.getAmount())
                .month(request.getMonth())
                .year(request.getYear())
                .build();

        budget = budgetRepository.save(budget);
        return mapToResponse(budget, userId);
    }

    @Override
    @Transactional
    public BudgetResponse updateBudget(Long userId, Long id, BudgetRequest request) {
        Budget budget = getBudgetOrThrow(id, userId);
        Category category = getCategoryOrThrow(request.getCategoryId());

        budget.setCategory(category);
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        budget = budgetRepository.save(budget);
        return mapToResponse(budget, userId);
    }

    @Override
    @Transactional
    public void deleteBudget(Long userId, Long id) {
        Budget budget = getBudgetOrThrow(id, userId);
        budgetRepository.delete(budget);
    }

    @Override
    public BudgetResponse getBudgetById(Long userId, Long id) {
        Budget budget = getBudgetOrThrow(id, userId);
        return mapToResponse(budget, userId);
    }

    @Override
    public List<BudgetResponse> getBudgetsByMonth(Long userId, Integer month, Integer year) {
        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);
        return budgets.stream()
                .map(budget -> mapToResponse(budget, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<BudgetResponse> getBudgetProgress(Long userId, Integer month, Integer year) {
        return getBudgetsByMonth(userId, month, year);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Budget getBudgetOrThrow(Long id, Long userId) {
        return budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private BudgetResponse mapToResponse(Budget budget, Long userId) {
        // Calculate spent amount
        BigDecimal spent = transactionRepository.sumByCategoryAndMonthAndYear(
                userId,
                TransactionType.EXPENSE,
                budget.getCategory().getId(),
                budget.getMonth(),
                budget.getYear()
        );

        if (spent == null) {
            spent = BigDecimal.ZERO;
        }

        BigDecimal remaining = budget.getAmount().subtract(spent);
        Double percentageUsed = spent.divide(budget.getAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        return BudgetResponse.builder()
                .id(budget.getId())
                .category(mapCategoryToResponse(budget.getCategory()))
                .amount(budget.getAmount())
                .month(budget.getMonth())
                .year(budget.getYear())
                .spent(spent)
                .remaining(remaining)
                .percentageUsed(percentageUsed)
                .build();
    }

    private CategoryResponse mapCategoryToResponse(Category category) {
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
