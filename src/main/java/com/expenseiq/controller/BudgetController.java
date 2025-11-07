package com.expenseiq.controller;

import com.expenseiq.dto.request.BudgetRequest;
import com.expenseiq.dto.response.ApiResponse;
import com.expenseiq.dto.response.BudgetResponse;
import com.expenseiq.security.SecurityUser;
import com.expenseiq.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(
            @AuthenticationPrincipal SecurityUser currentUser,
            @Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.createBudget(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Budget created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.updateBudget(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id) {
        budgetService.deleteBudget(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Budget deleted successfully", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>> getBudget(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id) {
        BudgetResponse response = budgetService.getBudgetById(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Budget retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getBudgetsByMonth(
            @AuthenticationPrincipal SecurityUser currentUser,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        if (month == null) {
            month = LocalDate.now().getMonthValue();
        }
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        List<BudgetResponse> budgets = budgetService.getBudgetsByMonth(currentUser.getId(), month, year);
        return ResponseEntity.ok(ApiResponse.success("Budgets retrieved successfully", budgets));
    }

    @GetMapping("/progress")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getBudgetProgress(
            @AuthenticationPrincipal SecurityUser currentUser,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        if (month == null) {
            month = LocalDate.now().getMonthValue();
        }
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        List<BudgetResponse> progress = budgetService.getBudgetProgress(currentUser.getId(), month, year);
        return ResponseEntity.ok(ApiResponse.success("Budget progress retrieved successfully", progress));
    }
}
