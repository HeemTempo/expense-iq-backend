# 🎨 ExpenseIQ Backend - Visual Guide

## 📊 Complete System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT (Browser/Mobile)                   │
│                                                               │
│  React App sends:                                            │
│  POST /api/auth/login                                        │
│  Headers: { Content-Type: application/json }                │
│  Body: { email: "user@example.com", password: "pass123" }   │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP Request
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              SPRING BOOT APPLICATION (Port 8081)             │
│                                                               │
│  ┌────────────────────────────────────────────────────────┐ │
│  │         SECURITY FILTER CHAIN                          │ │
│  │                                                          │ │
│  │  1. JwtAuthenticationFilter                            │ │
│  │     - Extracts JWT from Authorization header           │ │
│  │     - Validates token signature                        │ │
│  │     - Sets authentication in SecurityContext           │ │
│  │                                                          │ │
│  │  2. Spring Security Filters                            │ │
│  │     - CORS filter                                       │ │
│  │     - Exception handling                                │ │
│  │     - Authorization checks                              │ │
│  └────────────────────────────────────────────────────────┘ │
│                         │                                     │
│                         ▼                                     │
│  ┌────────────────────────────────────────────────────────┐ │
│  │              CONTROLLER LAYER                          │ │
│  │                                                          │ │
│  │  @RestController                                        │ │
│  │  @RequestMapping("/api/auth")                          │ │
│  │  public class AuthController {                         │ │
│  │                                                          │ │
│  │    @PostMapping("/login")                              │ │
│  │    public ResponseEntity<AuthResponse> login(          │ │
│  │        @Valid @RequestBody LoginRequest request) {     │ │
│  │      // Calls service layer                            │ │
│  │    }                                                     │ │
│  │  }                                                       │ │
│  └────────────────────────────────────────────────────────┘ │
│                         │                                     │
│                         ▼                                     │
│  ┌────────────────────────────────────────────────────────┐ │
│  │               SERVICE LAYER                            │ │
│  │                                                          │ │
│  │  @Service                                               │ │
│  │  public class AuthServiceImpl {                        │ │
│  │                                                          │ │
│  │    public AuthResponse login(LoginRequest request) {   │ │
│  │      // 1. Authenticate user                           │ │
│  │      // 2. Generate JWT token                          │ │
│  │      // 3. Return response                             │ │
│  │    }                                                     │ │
│  │  }                                                       │ │
│  └────────────────────────────────────────────────────────┘ │
│                         │                                     
│                         ▼                                     │
│  ┌────────────────────────────────────────────────────────┐ │
│  │            REPOSITORY LAYER                            │ │
│  │                                                          │ │
│  │  @Repository                                            │ │
│  │  public interface UserRepository                       │ │
│  │      extends JpaRepository<User, Long> {               │ │
│  │                                                          │ │
│  │    Optional<User> findByEmail(String email);           │ │
│  │  }                                                       │ │
│  └────────────────────────────────────────────────────────┘ │
│                         │                                     │
└─────────────────────────┼─────────────────────────────────────┘
                          │ JDBC
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                  POSTGRESQL DATABASE                         │
│                                                               │
│  Tables:                                                     │
│  ├── users                                                   │
│  ├── categories                                              │
│  ├── accounts                                                │
│  ├── transactions                                            │
│  ├── budgets                                                 │
│  ├── goals                                                   │
│  ├── recurring_transactions                                  │
│  └── notifications                                           │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Request Flow - Step by Step

### **Example: Create Transaction**

```
┌──────────┐
│  CLIENT  │
└────┬─────┘
     │
     │ 1. POST /api/transactions
     │    Authorization: Bearer eyJhbGc...
     │    Body: { type: "EXPENSE", amount: 50.00, ... }
     │
     ▼
┌────────────────────────────────────────┐
│  JwtAuthenticationFilter               │
│  --------------------------------      │
│  • Extract token from header           │
│  • Validate: tokenProvider.validate()  │
│  • Get email from token                │
│  • Load user from database             │
│  • Set SecurityContext                 │
└────┬───────────────────────────────────┘
     │ User authenticated ✓ration th
     ▼
┌────────────────────────────────────────┐
│  TransactionController                 │
│  --------------------------------      │
│  @PostMapping                          │
│  • Receives HTTP request               │
│  • Validates @Valid request body       │
│  • Gets current user from              │
│    @AuthenticationPrincipal            │
│  • Calls service layer                 │
└────┬───────────────────────────────────┘
     │
     ▼
┌────────────────────────────────────────┐
│  TransactionServiceImpl                │
│  --------------------------------      │
│  • Validates category exists           │
│  • Validates account exists            │
│  • Creates Transaction entity          │
│  • Updates account balance             │
│  • Saves via repository                │
└────┬───────────────────────────────────┘
     │
     ▼
┌────────────────────────────────────────┐
│  TransactionRepository                 │
│  --------------------------------      │
│  • save(transaction)                   │
│  • JPA converts to SQL INSERT          │
│  • Executes query                      │
└────┬───────────────────────────────────┘
     │
     ▼
┌────────────────────────────────────────┐
│  PostgreSQL Database                   │
│  --------------------------------      │
│  INSERT INTO transactions              │
│  (user_id, type, amount, ...)          │
│  VALUES (1, 'EXPENSE', 50.00, ...)     │
└────┬───────────────────────────────────┘
     │ Transaction saved ✓
     │
     │ Response flows back up ⬆
     │
     ▼
┌────────────────────────────────────────┐
│  CLIENT receives:                      │
│  --------------------------------      │
│  {                                     │
│    "success": true,                    │
│    "message": "Transaction created",   │
│    "data": {                           │
│      "id": 1,                          │
│      "type": "EXPENSE",                │
│      "amount": 50.00,                  │
│      ...                               │
│    }                                   │
│  }                                     │
└────────────────────────────────────────┘
```

---

## 🗄️ Database Schema Visual

```
┌─────────────────────────────────────────────────────────────┐
│                         USERS TABLE                          │
├──────────────┬──────────────┬────────────────────────────────┤
│ id (PK)      │ BIGINT       │ Primary Key                    │
│ email        │ VARCHAR(255) │ UNIQUE, NOT NULL               │
│ password     │ VARCHAR(255) │ BCrypt hashed                  │
│ name         │ VARCHAR(100) │ User's full name               │
│ role         │ VARCHAR(20)  │ USER or ADMIN                  │
│ currency     │ VARCHAR(3)   │ USD, EUR, etc.                 │
│ enabled      │ BOOLEAN      │ Account active?                │
│ created_at   │ TIMESTAMP    │ Auto-set on create             │
│ updated_at   │ TIMESTAMP    │ Auto-set on update             │
└──────────────┴──────────────┴────────────────────────────────┘
                    │
                    │ One user has many ↓
                    │
        ┌───────────┼───────────┬───────────┬───────────┐
        │           │           │           │           │
        ▼           ▼           ▼           ▼           ▼
┌──────────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│ TRANSACTIONS │ │ ACCOUNTS │ │ BUDGETS  │ │  GOALS   │ │CATEGORIES│
├──────────────┤ ├──────────┤ ├──────────┤ ├──────────┤ ├──────────┤
│ id (PK)      │ │ id (PK)  │ │ id (PK)  │ │ id (PK)  │ │ id (PK)  │
│ user_id (FK) │ │user_id(FK│ │user_id(FK│ │user_id(FK│ │user_id(FK│
│ category_id  │ │ name     │ │category  │ │ name     │ │ name     │
│ account_id   │ │ type     │ │ amount   │ │ target   │ │ type     │
│ type         │ │ balance  │ │ month    │ │ current  │ │ icon     │
│ amount       │ │ credit   │ │ year     │ │ deadline │ │ color    │
│ date         │ └──────────┘ └──────────┘ │completed │ │is_default│
│ description  │                           └──────────┘ └──────────┘
└──────────────┘

RELATIONSHIPS:
• User → has many → Transactions (1:N)
• User → has many → Accounts (1:N)
• User → has many → Budgets (1:N)
• User → has many → Goals (1:N)
• User → has many → Categories (1:N)
• Transaction → belongs to → User (N:1)
• Transaction → belongs to → Category (N:1)
• Transaction → belongs to → Account (N:1)
• Budget → belongs to → User (N:1)
• Budget → belongs to → Category (N:1)
```

---

## 🔐 JWT Token Structure

```
┌─────────────────────────────────────────────────────────────┐
│                    JWT TOKEN STRUCTURE                       │
└─────────────────────────────────────────────────────────────┘

Full Token:
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJpYXQiOjE2MzA0NTY3ODksImV4cCI6MTYzMDU0MzE4OX0.signature

Broken down:

┌──────────────────────────────────────┐
│  HEADER (Base64 encoded)             │
│  eyJhbGciOiJIUzUxMiJ9                │
│                                      │
│  Decoded:                            │
│  {                                   │
│    "alg": "HS512",                   │
│    "typ": "JWT"                      │
│  }                                   │
└──────────────────────────────────────┘
                .
┌──────────────────────────────────────┐
│  PAYLOAD (Base64 encoded)            │
│  eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29t... │
│                                      │
│  Decoded:                            │
│  {                                   │
│    "sub": "user@example.com",        │
│    "userId": 1,                      │
│    "iat": 1630456789,  ← Issued at  │
│    "exp": 1630543189   ← Expires    │
│  }                                   │
└──────────────────────────────────────┘
                .
┌──────────────────────────────────────┐
│  SIGNATURE                           │
│  HMACSHA512(                         │
│    base64(header) + "." +            │
│    base64(payload),                  │
│    secret_key                        │
│  )                                   │
│                                      │
│  Ensures token hasn't been modified  │
└──────────────────────────────────────┘
```

---

## 🔄 Authentication Flow Visual

### **Registration:**

```
CLIENT                    BACKEND                   DATABASE
  │                          │                          │
  │  POST /api/auth/register │                          │
  │  { name, email, pass }   │                          │
  ├─────────────────────────>│                          │
  │                          │                          │
  │                          │ Check email exists?      │
  │                          ├─────────────────────────>│
  │                          │<─────────────────────────┤
  │                          │ Email not found ✓        │
  │                          │                          │
  │                          │ Hash password (BCrypt)   │
  │                          │ "pass123" →              │
  │                          │ "$2a$10$N9qo8uLO..."     │
  │                          │                          │
  │                          │ Save user                │
  │                          ├─────────────────────────>│
  │                          │<─────────────────────────┤
  │                          │ User saved, id=1         │
  │                          │                          │
  │                          │ Generate JWT token       │
  │                          │ Sign with secret key     │
  │                          │                          │
  │  { token, refreshToken } │                          │
  │<─────────────────────────┤                          │
  │                          │                          │
  │ Store tokens locally     │                          │
  │                          │                          │
```

### **Login:**

```
CLIENT                    BACKEND                   DATABASE
  │                          │                          │
  │  POST /api/auth/login    │                          │
  │  { email, password }     │                          │
  ├─────────────────────────>│                          │
  │                          │                          │
  │                          │ Find user by email       │
  │                          ├─────────────────────────>│
  │                          │<─────────────────────────┤
  │                          │ User found ✓             │
  │                          │                          │
  │                          │ Compare passwords:       │
  │                          │ BCrypt.matches(          │
  │                          │   "pass123",             │
  │                          │   "$2a$10$N9qo8uLO..."   │
  │                          │ ) → true ✓               │
  │                          │                          │
  │                          │ Generate JWT token       │
  │                          │                          │
  │  { token, refreshToken } │                          │
  │<─────────────────────────┤                          │
  │                          │                          │
  │ Store tokens locally     │                          │
  │                          │                          │
```

### **Protected Request:**

```
CLIENT                    BACKEND                   DATABASE
  │                          │                          │
  │  GET /api/transactions   │                          │
  │  Authorization: Bearer   │                          │
  │  eyJhbGciOiJIUzUxMiJ9...  │                          │
  ├─────────────────────────>│                          │
  │                          │                          │
  │                    JwtAuthenticationFilter          │
  │                          │                          │
  │                          │ Extract token            │
  │                          │ Validate signature ✓     │
  │                          │ Check expiration ✓       │
  │                          │ Get email from token     │
  │                          │                          │
  │                          │ Load user                │
  │                          ├─────────────────────────>│
  │                          │<─────────────────────────┤
  │                          │ User found ✓             │
  │                          │                          │
  │                          │ Set authentication       │
  │                          │ in SecurityContext       │
  │                          │                          │
  │                    TransactionController            │
  │                          │                          │
  │                          │ Get transactions         │
  │                          ├─────────────────────────>│
  │                          │<─────────────────────────┤
  │                          │ Transactions list        │
  │                          │                          │
  │  { success, data: [...] }│                          │
  │<─────────────────────────┤                          │
  │                          │                          │
```

---

## 📦 Project Structure Visual

```
expense-iq-backend/
│
├── 📁 src/main/java/com/expenseiq/
│   │
│   ├── 📄 ExpenseIqApplication.java ← Main entry point
│   │
│   ├── 📁 config/                   ← Configuration classes
│   │   ├── CorsConfig.java          ← CORS settings
│   │   ├── DataInitializer.java     ← Default data
│   │   ├── JpaConfig.java           ← JPA settings
│   │   ├── OpenApiConfig.java       ← Swagger docs
│   │   └── SecurityConfig.java      ← Security rules
│   │
│   ├── 📁 controller/                ← REST API endpoints
│   │   ├── AccountController.java   ← /api/accounts
│   │   ├── AuthController.java      ← /api/auth
│   │   ├── BudgetController.java    ← /api/budgets
│   │   ├── CategoryController.java  ← /api/categories
│   │   ├── GoalController.java      ← /api/goals
│   │   └── TransactionController.java ← /api/transactions
│   │
│   ├── 📁 dto/                       ← Data transfer objects
│   │   ├── 📁 request/               ← From client
│   │   └── 📁 response/              ← To client
│   │
│   ├── 📁 entity/                    ← Database tables
│   │   ├── Account.java
│   │   ├── Budget.java
│   │   ├── Category.java
│   │   ├── Goal.java
│   │   ├── Transaction.java
│   │   └── User.java
│   │
│   ├── 📁 enums/                     ← Enumeration types
│   │   ├── AccountType.java
│   │   ├── Role.java
│   │   └── TransactionType.java
│   │
│   ├── 📁 exception/                 ← Error handling
│   │   ├── GlobalExceptionHandler.java
│   │   └── [Custom exceptions]
│   │
│   ├── 📁 repository/                ← Database access
│   │   ├── AccountRepository.java
│   │   ├── TransactionRepository.java
│   │   └── UserRepository.java
│   │
│   ├── 📁 security/                  ← Authentication
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtTokenProvider.java
│   │   └── SecurityUser.java
│   │
│   └── 📁 service/                   ← Business logic
│       ├── AccountService.java
│       ├── AuthService.java
│       ├── TransactionService.java
│       └── 📁 impl/                  ← Implementations
│
├── 📁 src/main/resources/
│   ├── application.yml               ← Main config
│   ├── application-postgres.yml      ← DB config
│   └── application-prod.yml          ← Production
│
├── 📁 instructions/                  ← THIS DOCUMENTATION
│   ├── README.md
│   ├── 01-PROJECT-OVERVIEW.md
│   ├── 02-CONFIGURATION-FILES.md
│   ├── 03-SECURITY-LAYER.md
│   └── VISUAL-GUIDE.md (this file)
│
└── 📄 pom.xml                        ← Maven dependencies
```

---

## 🎯 API Endpoints Map

```
┌─────────────────────────────────────────────────────────────┐
│                    API ENDPOINTS (31 total)                  │
└─────────────────────────────────────────────────────────────┘

🔐 AUTHENTICATION (Public - No token required)
├── POST   /api/auth/register       Register new user
├── POST   /api/auth/login          Login user
└── POST   /api/auth/refresh-token  Refresh JWT token

💳 TRANSACTIONS (Protected - Token required)
├── POST   /api/transactions         Create transaction
├── GET    /api/transactions         List all (with filters)
├── GET    /api/transactions/{id}    Get single transaction
├── PUT    /api/transactions/{id}    Update transaction
├── DELETE /api/transactions/{id}    Delete transaction
├── GET    /api/transactions/summary Get income/expense totals
└── GET    /api/transactions/recent  Get recent transactions

🏷️ CATEGORIES (Protected)
├── GET    /api/categories           List all categories
├── POST   /api/categories           Create custom category
├── GET    /api/categories/{id}      Get single category
├── PUT    /api/categories/{id}      Update category
└── DELETE /api/categories/{id}      Delete category

💼 ACCOUNTS (Protected)
├── GET    /api/accounts             List all accounts
├── POST   /api/accounts             Create account
├── GET    /api/accounts/{id}        Get single account
├── PUT    /api/accounts/{id}        Update account
└── DELETE /api/accounts/{id}        Delete account

🎯 BUDGETS (Protected)
├── GET    /api/budgets              List budgets by month
├── POST   /api/budgets              Create budget
├── GET    /api/budgets/{id}         Get single budget
├── PUT    /api/budgets/{id}         Update budget
├── DELETE /api/budgets/{id}         Delete budget
└── GET    /api/budgets/progress     Get budget progress

💰 GOALS (Protected)
├── GET    /api/goals                List all goals
├── POST   /api/goals                Create goal
├── GET    /api/goals/{id}           Get single goal
├── PUT    /api/goals/{id}           Update goal
├── DELETE /api/goals/{id}           Delete goal
└── POST   /api/goals/{id}/contribute Add money to goal
```

---

## 🎓 Key Concepts Visualized

### **Dependency Injection:**

```
❌ WITHOUT Dependency Injection:
┌──────────────────────────────┐
│  TransactionService          │
│  ──────────────────────────  │
│  public TransactionService() {
│    this.repository =         │
│      new TransactionRepo();  │  ← Tightly coupled
│  }                           │
└──────────────────────────────┘

✅ WITH Dependency Injection:
┌──────────────────────────────┐
│  @Service                    │
│  @RequiredArgsConstructor    │
│  TransactionService {        │
│    private final             │
│      TransactionRepository   │
│      repository;  ← Spring   │
│  }                  injects  │
└──────────────────────────────┘
```

### **Layered Architecture:**

```
┌─────────────────────────────────────┐
│  CONTROLLER                         │  ← HTTP Layer
│  • Receives HTTP requests           │
│  • Validates input                  │
│  • Returns HTTP responses           │
└────────────┬────────────────────────┘
             │ Calls
             ▼
┌─────────────────────────────────────┐
│  SERVICE                            │  ← Business Logic
│  • Business rules                   │
│  • Validation                       │
│  • Orchestration                    │
└────────────┬────────────────────────┘
             │ Calls
             ▼
┌─────────────────────────────────────┐
│  REPOSITORY                         │  ← Data Access
│  • Database queries                 │
│  • CRUD operations                  │
│  • Custom queries                   │
└────────────┬────────────────────────┘
             │ JDBC
             ▼
┌─────────────────────────────────────┐
│  DATABASE                           │  ← Data Storage
│  • PostgreSQL                       │
│  • Tables                           │
│  • Relationships                    │
└─────────────────────────────────────┘
```

---

**This visual guide complements the detailed line-by-line documentation!**

**Next:** Read the detailed explanations in numbered files! 📚
