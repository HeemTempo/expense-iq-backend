package com.expenseiq.dto.request;

import com.expenseiq.enums.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Account type is required")
    private AccountType type;

    @DecimalMin(value = "0.0", message = "Balance cannot be negative")
    private BigDecimal balance = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Credit limit cannot be negative")
    private BigDecimal creditLimit;
}
