package com.expenseiq.service;

import com.expenseiq.dto.request.BudgetRequest;
import com.expenseiq.dto.response.BudgetResponse;

import java.util.List;

public interface BudgetService {
    BudgetResponse createBudget(Long userId, BudgetRequest request);
    BudgetResponse updateBudget(Long userId, Long id, BudgetRequest request);
    void deleteBudget(Long userId, Long id);
    BudgetResponse getBudgetById(Long userId, Long id);
    List<BudgetResponse> getBudgetsByMonth(Long userId, Integer month, Integer year);
    List<BudgetResponse> getBudgetProgress(Long userId, Integer month, Integer year);
}
