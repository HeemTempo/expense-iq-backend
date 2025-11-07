package com.expenseiq.dto.response;

import com.expenseiq.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private String receiptUrl;
    private Boolean isRecurring;
    private CategoryResponse category;
    private AccountResponse account;
    private LocalDateTime createdAt;
}
