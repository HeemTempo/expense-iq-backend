# 🚀 ExpenseIQ Backend - Quick Reference

## 📂 Project Structure Overview

```
expense-iq-backend/
├── src/main/java/com/expenseiq/
│   ├── ExpenseIqApplication.java      ← Main entry point
│   ├── entity/                        ← Database tables (8 entities)
│   │   ├── User.java
│   │   ├── Transaction.java
│   │   ├── Category.java
│   │   ├── Account.java
│   │   ├── Budget.java
│   │   ├── Goal.java
│   │   ├── RecurringTransaction.java
│   │   └── Notification.java
│   ├── enums/                         ← Enums (5 types)
│   ├── config/                        ← Configuration (TODO)
│   ├── controller/                    ← REST endpoints (TODO)
│   ├── service/                       ← Business logic (TODO)
│   ├── repository/                    ← Database access (TODO)
│   ├── dto/                           ← Request/Response objects (TODO)
│   ├── security/                      ← JWT & Security (TODO)
│   └── exception/                     ← Error handling (TODO)
└── src/main/resources/
    ├── application.yml                ← Main config
    ├── application-dev.yml            ← Dev config (H2)
    └── application-prod.yml           ← Prod config (MySQL)
```

---

## 🗄️ Database Entities

### 1. User
```java
- id: Long
- email: String (unique)
- password: String (hashed)
- name: String
- profilePicture: String
- currency: String (default: "USD")
- role: Role (USER/ADMIN)
- enabled: Boolean
```

### 2. Transaction
```java
- id: Long
- user: User
- account: Account
- category: Category
- type: TransactionType (INCOME/EXPENSE)
- amount: BigDecimal
- description: String
- date: LocalDate
- receiptUrl: String
- isRecurring: Boolean
```

### 3. Category
```java
- id: Long
- user: User (null for default categories)
- name: String
- type: TransactionType
- icon: String
- color: String
- isDefault: Boolean
```

### 4. Account
```java
- id: Long
- user: User
- name: String
- type: AccountType (CASH/BANK/CREDIT_CARD/SAVINGS/INVESTMENT)
- balance: BigDecimal
- creditLimit: BigDecimal
```

### 5. Budget
```java
- id: Long
- user: User
- category: Category
- amount: BigDecimal
- month: Integer
- year: Integer
```

### 6. Goal
```java
- id: Long
- user: User
- name: String
- targetAmount: BigDecimal
- currentAmount: BigDecimal
- deadline: LocalDate
- icon: String
- completed: Boolean
```

### 7. RecurringTransaction
```java
- id: Long
- user: User
- account: Account
- category: Category
- type: TransactionType
- amount: BigDecimal
- description: String
- frequency: Frequency (DAILY/WEEKLY/MONTHLY/YEARLY)
- nextDate: LocalDate
- isActive: Boolean
```

### 8. Notification
```java
- id: Long
- user: User
- title: String
- message: String
- type: NotificationType
- isRead: Boolean
```

---

## 🔧 Configuration Files

### application.yml (Main)
```yaml
server:
  port: 8080

spring:
  profiles:
    active: dev

jwt:
  secret: YourSuperSecretKey
  expiration: 86400000  # 24 hours

cors:
  allowed-origins: http://localhost:5173
```

### application-dev.yml (Development)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:expenseiq
    username: sa
    password: 
  h2:
    console:
      enabled: true
```

### application-prod.yml (Production)
```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
```

---

## 🎯 Common Maven Commands

```bash
# Run application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Build JAR file
mvn clean package

# Run tests
mvn test

# Skip tests during build
mvn clean package -DskipTests

# Clean build
mvn clean install
```

---

## 🔐 Authentication Flow (To be implemented)

```
1. User Registration
   POST /api/auth/register
   → Hash password
   → Save user
   → Return success

2. User Login
   POST /api/auth/login
   → Verify credentials
   → Generate JWT token
   → Return token

3. Protected Request
   GET /api/transactions
   Header: Authorization: Bearer <token>
   → Validate token
   → Extract user
   → Process request
```

---

## 📝 API Endpoint Structure (Planned)

### Authentication
```
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/refresh-token
```

### Transactions
```
GET    /api/transactions
GET    /api/transactions/{id}
POST   /api/transactions
PUT    /api/transactions/{id}
DELETE /api/transactions/{id}
GET    /api/transactions/summary
```

### Categories
```
GET    /api/categories
POST   /api/categories
PUT    /api/categories/{id}
DELETE /api/categories/{id}
```

### Accounts
```
GET    /api/accounts
POST   /api/accounts
PUT    /api/accounts/{id}
DELETE /api/accounts/{id}
POST   /api/accounts/transfer
```

### Budgets
```
GET    /api/budgets
POST   /api/budgets
PUT    /api/budgets/{id}
DELETE /api/budgets/{id}
GET    /api/budgets/progress
```

### Goals
```
GET    /api/goals
POST   /api/goals
PUT    /api/goals/{id}
DELETE /api/goals/{id}
POST   /api/goals/{id}/contribute
```

### Reports
```
GET    /api/reports/dashboard
GET    /api/reports/monthly-trend
GET    /api/reports/category-breakdown
```

---

## 🧪 Testing URLs

### H2 Console (Development)
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:expenseiq
Username: sa
Password: (empty)
```

### Swagger UI (After implementation)
```
URL: http://localhost:8080/swagger-ui.html
```

### API Docs
```
URL: http://localhost:8080/api-docs
```

---

## 🐛 Common Errors & Solutions

### Port already in use
```bash
# Error: Port 8080 is already in use
# Solution: Change port in application.yml
server:
  port: 8081
```

### Cannot find main class
```bash
# Solution: Rebuild project
mvn clean install
```

### Database connection error
```bash
# Check application.yml database settings
# For dev, make sure H2 is configured correctly
```

### Lombok not working
```bash
# Solution: Enable annotation processing in IDE
# IntelliJ: Settings → Build → Compiler → Annotation Processors
```

---

## 📦 Dependencies (pom.xml)

```xml
<!-- Core -->
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-validation

<!-- Database -->
mysql-connector-j
h2database

<!-- JWT -->
jjwt-api
jjwt-impl
jjwt-jackson

<!-- Utilities -->
lombok
springdoc-openapi-starter-webmvc-ui

<!-- Testing -->
spring-boot-starter-test
spring-security-test
```

---

## 🎨 Code Patterns

### Controller Pattern
```java
@RestController
@RequestMapping("/api/resource")
public class ResourceController {
    
    @GetMapping
    public ResponseEntity<List<Resource>> getAll() {
        // Implementation
    }
    
    @PostMapping
    public ResponseEntity<Resource> create(@RequestBody ResourceRequest request) {
        // Implementation
    }
}
```

### Service Pattern
```java
@Service
public class ResourceService {
    
    @Autowired
    private ResourceRepository repository;
    
    public Resource create(ResourceRequest request) {
        // Business logic
        return repository.save(resource);
    }
}
```

### Repository Pattern
```java
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByUserId(Long userId);
    Optional<Resource> findByUserIdAndId(Long userId, Long id);
}
```

---

## 🔑 Environment Variables (Production)

```bash
# JWT
JWT_SECRET=your-super-secret-key-min-256-bits

# Database
DATABASE_URL=jdbc:mysql://localhost:3306/expenseiq
DATABASE_USERNAME=root
DATABASE_PASSWORD=your_password

# Server
SERVER_PORT=8080
```

---

## 📚 Useful Links

- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **Spring Security**: https://spring.io/projects/spring-security
- **JWT.io**: https://jwt.io/
- **Lombok**: https://projectlombok.org/
- **H2 Database**: https://www.h2database.com/

---

## ✅ Current Status

**Phase 1: Foundation** ✅ COMPLETE
- Project structure created
- All entities defined
- Configuration files ready
- Ready for Phase 2: Authentication

**Next: Implement JWT Authentication**

---

## 💡 Quick Tips

1. **Always test in Postman/Swagger** after creating endpoints
2. **Use H2 console** to verify database changes
3. **Check logs** for detailed error messages
4. **Use @Transactional** for methods that modify data
5. **Validate input** with @Valid and validation annotations
6. **Handle exceptions** with @ControllerAdvice
7. **Use DTOs** instead of exposing entities directly

---

**Need help? Check:**
- `README.md` - Project overview
- `GETTING_STARTED.md` - Beginner's guide
- `ROADMAP.md` - Development phases

**Ready to code? Let's build Phase 2! 🚀**
