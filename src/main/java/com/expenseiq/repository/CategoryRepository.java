package com.expenseiq.repository;

import com.expenseiq.entity.Category;
import com.expenseiq.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByUserIdOrIsDefaultTrue(Long userId);
    
    List<Category> findByUserIdAndType(Long userId, TransactionType type);
    
    @Query("SELECT c FROM Category c WHERE (c.user.id = :userId OR c.isDefault = true) AND c.type = :type")
    List<Category> findByUserIdOrDefaultAndType(Long userId, TransactionType type);
    
    Optional<Category> findByIdAndUserId(Long id, Long userId);
    
    List<Category> findByIsDefaultTrue();
    
    boolean existsByIdAndUserId(Long id, Long userId);
}
