package com.expenseiq.dto.request;

import com.expenseiq.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 50, message = "Category name must not exceed 50 characters")
    private String name;

    @NotNull(message = "Category type is required")
    private TransactionType type;

    @Size(max = 50, message = "Icon must not exceed 50 characters")
    private String icon;

    @Size(max = 7, message = "Color must not exceed 7 characters")
    private String color;
}
