package com.expenseiq.repository;

import com.expenseiq.entity.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    
    List<RecurringTransaction> findByUserId(Long userId);
    
    List<RecurringTransaction> findByUserIdAndIsActiveTrue(Long userId);
    
    List<RecurringTransaction> findByIsActiveTrueAndNextDateLessThanEqual(LocalDate date);
    
    Optional<RecurringTransaction> findByIdAndUserId(Long id, Long userId);
    
    boolean existsByIdAndUserId(Long id, Long userId);
}
