# 🎯 ExpenseIQ Backend - Complete Beginner's Guide

Welcome! This guide will help you understand and build the ExpenseIQ backend step by step.

## 📚 What You'll Learn

1. Spring Boot fundamentals
2. REST API design
3. JWT authentication
4. Database design with JPA
5. Service layer architecture
6. Exception handling
7. API documentation

## 🗺️ Development Roadmap

### Phase 1: Foundation ✅ (COMPLETED)
- [x] Project structure created
- [x] Maven dependencies configured
- [x] Database entities created
- [x] Enums defined
- [x] Application configuration

### Phase 2: Authentication & Security (NEXT)
**What we'll build:**
- JWT token generation and validation
- User registration
- User login
- Password encryption
- Protected endpoints

**Files to create:**
1. `JwtTokenProvider.java` - Generate and validate JWT tokens
2. `JwtAuthenticationFilter.java` - Intercept requests and validate tokens
3. `SecurityConfig.java` - Configure Spring Security
4. `AuthService.java` - Handle registration and login logic
5. `AuthController.java` - REST endpoints for auth

**Learning goals:**
- Understand JWT authentication flow
- Learn Spring Security configuration
- Password hashing with BCrypt

### Phase 3: Core Features - Transactions
**What we'll build:**
- Create, read, update, delete transactions
- Filter transactions by date, category, type
- Upload receipt images
- Calculate totals

**Files to create:**
1. `TransactionRepository.java` - Database queries
2. `TransactionService.java` - Business logic
3. `TransactionController.java` - REST endpoints
4. `TransactionRequest.java` - Input validation
5. `TransactionResponse.java` - Output format

**API Endpoints:**
```
POST   /api/transactions          - Create transaction
GET    /api/transactions          - List all (with filters)
GET    /api/transactions/{id}     - Get single transaction
PUT    /api/transactions/{id}     - Update transaction
DELETE /api/transactions/{id}     - Delete transaction
```

### Phase 4: Categories & Accounts
**What we'll build:**
- Default categories (Food, Rent, Salary, etc.)
- Custom user categories
- Multiple accounts (Cash, Bank, Credit Card)
- Account balance tracking

### Phase 5: Budget Management
**What we'll build:**
- Set monthly budgets per category
- Track spending vs budget
- Budget alerts (90% used)
- Budget progress visualization

### Phase 6: Goals & Savings
**What we'll build:**
- Create savings goals
- Track progress
- Add contributions
- Goal completion notifications

### Phase 7: Reports & Analytics
**What we'll build:**
- Dashboard summary (income, expense, balance)
- Monthly trends (last 12 months)
- Category breakdown
- Spending patterns
- Export to CSV/PDF

### Phase 8: Advanced Features
**What we'll build:**
- Recurring transactions (subscriptions, rent)
- Notifications system
- Email notifications
- File upload for receipts

## 🏗️ Architecture Overview

```
┌─────────────┐
│   Client    │ (React Frontend)
│  (Browser)  │
└──────┬──────┘
       │ HTTP Requests
       ▼
┌─────────────────────────────────┐
│      Controller Layer           │ ← REST endpoints
│  (AuthController, etc.)         │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│      Service Layer              │ ← Business logic
│  (AuthService, etc.)            │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│    Repository Layer             │ ← Database queries
│  (UserRepository, etc.)         │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│         Database                │
│      (MySQL / H2)               │
└─────────────────────────────────┘
```

## 🔄 Request Flow Example

**User Login Flow:**

1. **Client** sends POST request to `/api/auth/login`
   ```json
   {
     "email": "john@example.com",
     "password": "password123"
   }
   ```

2. **AuthController** receives request
   - Validates input
   - Calls `AuthService.login()`

3. **AuthService** processes login
   - Finds user in database
   - Verifies password (BCrypt)
   - Generates JWT token
   - Returns token

4. **Response** sent back to client
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     "user": {
       "id": 1,
       "name": "John Doe",
       "email": "john@example.com"
     }
   }
   ```

5. **Client** stores token and includes it in future requests
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

## 🎓 Key Concepts to Understand

### 1. **Entity** (Database Table)
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    
    private String email;
    private String password;
}
```
- Represents a database table
- Each instance = one row
- Annotations define structure

### 2. **Repository** (Database Access)
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```
- Interface for database operations
- Spring generates implementation automatically
- Methods like `save()`, `findById()`, `delete()` are built-in

### 3. **Service** (Business Logic)
```java
@Service
public class AuthService {
    public User register(RegisterRequest request) {
        // Validate email not taken
        // Hash password
        // Save user
        // Return user
    }
}
```
- Contains business rules
- Coordinates between repository and controller
- Reusable logic

### 4. **Controller** (REST API)
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok(user);
    }
}
```
- Handles HTTP requests
- Maps URLs to methods
- Returns HTTP responses

### 5. **DTO** (Data Transfer Object)
```java
public class RegisterRequest {
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 6)
    private String password;
}
```
- Defines input/output format
- Includes validation rules
- Separates API from database structure

## 🛠️ Next Steps

### To continue development:

1. **Install Java 17** (if not installed)
   - Download from: https://adoptium.net/

2. **Install Maven** (or use included wrapper)
   - Check: `mvn --version`

3. **Open project in IDE**
   - IntelliJ IDEA (recommended)
   - VS Code with Java extensions
   - Eclipse

4. **Run the application**
   ```bash
   cd expense-iq-backend
   mvn spring-boot:run
   ```

5. **Verify it's running**
   - Open browser: `http://localhost:8080`
   - Should see error page (normal - no endpoints yet!)
   - H2 Console: `http://localhost:8080/h2-console`

## 📖 Learning Resources

### Spring Boot
- [Official Spring Boot Guide](https://spring.io/guides/gs/spring-boot/)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)

### JWT Authentication
- [JWT.io](https://jwt.io/) - Understand JWT tokens
- [Spring Security Guide](https://spring.io/guides/topicals/spring-security-architecture/)

### JPA & Hibernate
- [Spring Data JPA Guide](https://spring.io/guides/gs/accessing-data-jpa/)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)

### REST API Design
- [REST API Tutorial](https://restfulapi.net/)
- [HTTP Status Codes](https://httpstatuses.com/)

## 💡 Tips for Beginners

1. **Start small** - Don't try to build everything at once
2. **Test as you go** - Use Postman or Swagger to test each endpoint
3. **Read error messages** - They usually tell you exactly what's wrong
4. **Use debugger** - Set breakpoints and step through code
5. **Ask questions** - Don't hesitate to ask when stuck!

## 🐛 Common Issues & Solutions

### Issue: Port 8080 already in use
**Solution:** Change port in `application.yml`
```yaml
server:
  port: 8081
```

### Issue: Database connection error
**Solution:** Check database credentials in `application.yml`

### Issue: JWT token expired
**Solution:** Login again to get new token

### Issue: 403 Forbidden
**Solution:** Check if you included Authorization header

## 🎯 What's Next?

Let me know when you're ready, and I'll help you build:

1. **JWT Authentication** (recommended next step)
2. **Transaction CRUD operations**
3. **Any specific feature you want to start with**

Would you like me to:
- ✅ Create the JWT authentication system?
- ✅ Set up the first REST endpoints?
- ✅ Add example data for testing?
- ✅ Create Postman collection for testing?

Just let me know what you'd like to tackle first! 🚀
