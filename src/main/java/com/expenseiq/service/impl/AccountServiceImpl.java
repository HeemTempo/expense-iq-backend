package com.expenseiq.service.impl;

import com.expenseiq.dto.request.AccountRequest;
import com.expenseiq.dto.response.AccountResponse;
import com.expenseiq.entity.Account;
import com.expenseiq.entity.User;
import com.expenseiq.exception.ResourceNotFoundException;
import com.expenseiq.repository.AccountRepository;
import com.expenseiq.repository.UserRepository;
import com.expenseiq.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AccountResponse createAccount(Long userId, AccountRequest request) {
        User user = getUserOrThrow(userId);

        Account account = Account.builder()
                .user(user)
                .name(request.getName())
                .type(request.getType())
                .balance(request.getBalance())
                .creditLimit(request.getCreditLimit())
                .build();

        account = accountRepository.save(account);
        return mapToResponse(account);
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(Long userId, Long id, AccountRequest request) {
        Account account = getAccountOrThrow(id, userId);

        account.setName(request.getName());
        account.setType(request.getType());
        account.setBalance(request.getBalance());
        account.setCreditLimit(request.getCreditLimit());

        account = accountRepository.save(account);
        return mapToResponse(account);
    }

    @Override
    @Transactional
    public void deleteAccount(Long userId, Long id) {
        Account account = getAccountOrThrow(id, userId);
        accountRepository.delete(account);
    }

    @Override
    public AccountResponse getAccountById(Long userId, Long id) {
        Account account = getAccountOrThrow(id, userId);
        return mapToResponse(account);
    }

    @Override
    public List<AccountResponse> getAllAccounts(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accounts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Account getAccountOrThrow(Long id, Long userId) {
        return accountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .type(account.getType())
                .balance(account.getBalance())
                .creditLimit(account.getCreditLimit())
                .build();
    }
}
