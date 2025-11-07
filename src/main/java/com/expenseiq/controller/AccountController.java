package com.expenseiq.controller;

import com.expenseiq.dto.request.AccountRequest;
import com.expenseiq.dto.response.AccountResponse;
import com.expenseiq.dto.response.ApiResponse;
import com.expenseiq.security.SecurityUser;
import com.expenseiq.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @AuthenticationPrincipal SecurityUser currentUser,
            @Valid @RequestBody AccountRequest request) {
        AccountResponse response = accountService.createAccount(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccount(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id,
            @Valid @RequestBody AccountRequest request) {
        AccountResponse response = accountService.updateAccount(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Account updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id) {
        accountService.deleteAccount(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id) {
        AccountResponse response = accountService.getAccountById(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Account retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAllAccounts(
            @AuthenticationPrincipal SecurityUser currentUser) {
        List<AccountResponse> accounts = accountService.getAllAccounts(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", accounts));
    }
}
