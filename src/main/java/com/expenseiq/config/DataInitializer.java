package com.expenseiq.config;

import com.expenseiq.entity.User;
import com.expenseiq.repository.UserRepository;
import com.expenseiq.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryService categoryService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Initialize default categories on application startup
        categoryService.initializeDefaultCategories();
        
        // Create test user if it doesn't exist
        if (userRepository.findByEmail("test@example.com").isEmpty()) {
            User testUser = User.builder()
                    .name("Test User")
                    .email("test@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .currency("USD")
                    .role(com.expenseiq.enums.Role.USER)
                    .enabled(true)
                    .build();
            
            userRepository.save(testUser);
            log.info("✅ Test user created - Email: test@example.com, Password: password123");
        }
    }
}
