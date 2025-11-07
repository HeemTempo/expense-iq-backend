package com.expenseiq.service;

import com.expenseiq.dto.request.TransactionRequest;
import com.expenseiq.dto.response.TransactionResponse;
import com.expenseiq.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TransactionService {
    TransactionResponse createTransaction(Long userId, TransactionRequest request);
    TransactionResponse updateTransaction(Long userId, Long id, TransactionRequest request);
    void deleteTransaction(Long userId, Long id);
    TransactionResponse getTransactionById(Long userId, Long id);
    Page<TransactionResponse> getAllTransactions(Long userId, Pageable pageable);
    Page<TransactionResponse> getTransactionsByFilters(
            Long userId,
            TransactionType type,
            Long categoryId,
            Long accountId,
            LocalDate startDate,
            LocalDate endDate,
            String description,
            Pageable pageable
    );
    Map<String, BigDecimal> getTransactionSummary(Long userId, LocalDate startDate, LocalDate endDate);
    List<TransactionResponse> getRecentTransactions(Long userId, int limit);
}
