package com.expenseiq.dto.response;

import com.expenseiq.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String name;
    private AccountType type;
    private BigDecimal balance;
    private BigDecimal creditLimit;
}
