package com.expenseiq.controller;

import com.expenseiq.dto.request.TransactionRequest;
import com.expenseiq.dto.response.ApiResponse;
import com.expenseiq.dto.response.TransactionResponse;
import com.expenseiq.enums.TransactionType;
import com.expenseiq.security.SecurityUser;
import com.expenseiq.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @AuthenticationPrincipal SecurityUser currentUser,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transaction created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.updateTransaction(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Transaction updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id) {
        transactionService.deleteTransaction(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransaction(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id) {
        TransactionResponse response = transactionService.getTransactionById(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Transaction retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getAllTransactions(
            @AuthenticationPrincipal SecurityUser currentUser,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date,desc") String[] sort) {

        Sort.Direction direction = sort.length > 1 && sort[1].equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        Page<TransactionResponse> transactions;
        if (type != null || categoryId != null || accountId != null || 
            startDate != null || endDate != null || description != null) {
            transactions = transactionService.getTransactionsByFilters(
                    currentUser.getId(), type, categoryId, accountId, 
                    startDate, endDate, description, pageable);
        } else {
            transactions = transactionService.getAllTransactions(currentUser.getId(), pageable);
        }

        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getTransactionSummary(
            @AuthenticationPrincipal SecurityUser currentUser,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        Map<String, BigDecimal> summary = transactionService.getTransactionSummary(
                currentUser.getId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Transaction summary retrieved successfully", summary));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getRecentTransactions(
            @AuthenticationPrincipal SecurityUser currentUser,
            @RequestParam(defaultValue = "5") int limit) {
        List<TransactionResponse> transactions = transactionService.getRecentTransactions(
                currentUser.getId(), limit);
        return ResponseEntity.ok(ApiResponse.success("Recent transactions retrieved successfully", transactions));
    }
}
