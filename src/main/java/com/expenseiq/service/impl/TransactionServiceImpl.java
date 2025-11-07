package com.expenseiq.service.impl;

import com.expenseiq.dto.request.TransactionRequest;
import com.expenseiq.dto.response.AccountResponse;
import com.expenseiq.dto.response.CategoryResponse;
import com.expenseiq.dto.response.TransactionResponse;
import com.expenseiq.entity.Account;
import com.expenseiq.entity.Category;
import com.expenseiq.entity.Transaction;
import com.expenseiq.entity.User;
import com.expenseiq.enums.TransactionType;
import com.expenseiq.exception.BadRequestException;
import com.expenseiq.exception.ResourceNotFoundException;
import com.expenseiq.repository.AccountRepository;
import com.expenseiq.repository.CategoryRepository;
import com.expenseiq.repository.TransactionRepository;
import com.expenseiq.repository.UserRepository;
import com.expenseiq.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public TransactionResponse createTransaction(Long userId, TransactionRequest request) {
        User user = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(request.getCategoryId(), userId);
        Account account = getAccountOrThrow(request.getAccountId(), userId);

        // Validate category type matches transaction type
        if (!category.getType().equals(request.getType())) {
            throw new BadRequestException("Category type does not match transaction type");
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .account(account)
                .category(category)
                .type(request.getType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .date(request.getDate())
                .receiptUrl(request.getReceiptUrl())
                .isRecurring(request.getIsRecurring())
                .build();

        transaction = transactionRepository.save(transaction);

        // Update account balance
        updateAccountBalance(account, request.getType(), request.getAmount());

        return mapToResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse updateTransaction(Long userId, Long id, TransactionRequest request) {
        Transaction transaction = getTransactionOrThrow(id, userId);
        Category category = getCategoryOrThrow(request.getCategoryId(), userId);
        Account account = getAccountOrThrow(request.getAccountId(), userId);

        // Validate category type matches transaction type
        if (!category.getType().equals(request.getType())) {
            throw new BadRequestException("Category type does not match transaction type");
        }

        // Revert old account balance
        updateAccountBalance(transaction.getAccount(), 
                transaction.getType().equals(TransactionType.INCOME) ? TransactionType.EXPENSE : TransactionType.INCOME,
                transaction.getAmount());

        // Update transaction
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        transaction.setReceiptUrl(request.getReceiptUrl());
        transaction.setIsRecurring(request.getIsRecurring());

        transaction = transactionRepository.save(transaction);

        // Apply new account balance
        updateAccountBalance(account, request.getType(), request.getAmount());

        return mapToResponse(transaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long userId, Long id) {
        Transaction transaction = getTransactionOrThrow(id, userId);

        // Revert account balance
        updateAccountBalance(transaction.getAccount(),
                transaction.getType().equals(TransactionType.INCOME) ? TransactionType.EXPENSE : TransactionType.INCOME,
                transaction.getAmount());

        transactionRepository.delete(transaction);
    }

    @Override
    public TransactionResponse getTransactionById(Long userId, Long id) {
        Transaction transaction = getTransactionOrThrow(id, userId);
        return mapToResponse(transaction);
    }

    @Override
    public Page<TransactionResponse> getAllTransactions(Long userId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByUserId(userId, pageable);
        return transactions.map(this::mapToResponse);
    }

    @Override
    public Page<TransactionResponse> getTransactionsByFilters(
            Long userId,
            TransactionType type,
            Long categoryId,
            Long accountId,
            LocalDate startDate,
            LocalDate endDate,
            String description,
            Pageable pageable) {
        
        Page<Transaction> transactions = transactionRepository.findByFilters(
                userId, type, categoryId, accountId, startDate, endDate, description, pageable
        );
        return transactions.map(this::mapToResponse);
    }

    @Override
    public Map<String, BigDecimal> getTransactionSummary(Long userId, LocalDate startDate, LocalDate endDate) {
        BigDecimal income = transactionRepository.sumByUserIdAndTypeAndDateBetween(
                userId, TransactionType.INCOME, startDate, endDate
        );
        BigDecimal expense = transactionRepository.sumByUserIdAndTypeAndDateBetween(
                userId, TransactionType.EXPENSE, startDate, endDate
        );

        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("income", income != null ? income : BigDecimal.ZERO);
        summary.put("expense", expense != null ? expense : BigDecimal.ZERO);
        summary.put("balance", (income != null ? income : BigDecimal.ZERO)
                .subtract(expense != null ? expense : BigDecimal.ZERO));

        return summary;
    }

    @Override
    public List<TransactionResponse> getRecentTransactions(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "date", "createdAt"));
        List<Transaction> transactions = transactionRepository.findRecentByUserId(userId, pageable);
        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper methods
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Transaction getTransactionOrThrow(Long id, Long userId) {
        return transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    private Category getCategoryOrThrow(Long categoryId, Long userId) {
        return categoryRepository.findById(categoryId)
                .filter(c -> c.getUser() == null || c.getUser().getId().equals(userId) || c.getIsDefault())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private Account getAccountOrThrow(Long accountId, Long userId) {
        return accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    @Transactional
    private void updateAccountBalance(Account account, TransactionType type, BigDecimal amount) {
        if (type == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(amount));
        } else {
            account.setBalance(account.getBalance().subtract(amount));
        }
        accountRepository.save(account);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .date(transaction.getDate())
                .receiptUrl(transaction.getReceiptUrl())
                .isRecurring(transaction.getIsRecurring())
                .category(mapCategoryToResponse(transaction.getCategory()))
                .account(mapAccountToResponse(transaction.getAccount()))
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    private CategoryResponse mapCategoryToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .icon(category.getIcon())
                .color(category.getColor())
                .isDefault(category.getIsDefault())
                .build();
    }

    private AccountResponse mapAccountToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .type(account.getType())
                .balance(account.getBalance())
                .creditLimit(account.getCreditLimit())
                .build();
    }
}
