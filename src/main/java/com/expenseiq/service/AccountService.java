package com.expenseiq.service;

import com.expenseiq.dto.request.AccountRequest;
import com.expenseiq.dto.response.AccountResponse;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(Long userId, AccountRequest request);
    AccountResponse updateAccount(Long userId, Long id, AccountRequest request);
    void deleteAccount(Long userId, Long id);
    AccountResponse getAccountById(Long userId, Long id);
    List<AccountResponse> getAllAccounts(Long userId);
}
