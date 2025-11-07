package com.expenseiq.repository;

import com.expenseiq.entity.Transaction;
import com.expenseiq.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Page<Transaction> findByUserId(Long userId, Pageable pageable);
    
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);
    
    List<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
           "AND (:accountId IS NULL OR t.account.id = :accountId) " +
           "AND (:startDate IS NULL OR t.date >= :startDate) " +
           "AND (:endDate IS NULL OR t.date <= :endDate) " +
           "AND (:description IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :description, '%')))")
    Page<Transaction> findByFilters(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("categoryId") Long categoryId,
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("description") String description,
            Pageable pageable
    );
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = :type " +
           "AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserIdAndTypeAndDateBetween(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = :type " +
           "AND t.category.id = :categoryId " +
           "AND EXTRACT(MONTH FROM t.date) = :month AND EXTRACT(YEAR FROM t.date) = :year")
    BigDecimal sumByCategoryAndMonthAndYear(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("categoryId") Long categoryId,
            @Param("month") int month,
            @Param("year") int year
    );
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);
    
    boolean existsByIdAndUserId(Long id, Long userId);
    
    boolean existsByCategoryId(Long categoryId);
}
