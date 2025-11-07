# 📚 ExpenseIQ Backend - Complete Project Overview

## 🎯 What This Project Does

**ExpenseIQ** is a Personal Finance Management System that helps users:
- Track income and expenses
- Manage multiple accounts (bank, cash, credit cards)
- Set and monitor budgets
- Create savings goals
- View financial reports and summaries

---

## 🏗️ Architecture Overview

### **Technology Stack**
- **Backend Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Database:** PostgreSQL (Development & Production)
- **Security:** Spring Security + JWT
- **API Documentation:** Swagger/OpenAPI
- **Build Tool:** Maven

### **Architecture Pattern**
```
┌─────────────────────────────────────────────────┐
│              REST API Layer                      │
│         (Controllers - HTTP Endpoints)           │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│           Service Layer                          │
│        (Business Logic)                          │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│        Repository Layer                          │
│      (Database Access - JPA)                     │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│          PostgreSQL Database                     │
│         (Data Storage)                           │
└─────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
expense-iq-backend/
├── src/main/java/com/expenseiq/
│   ├── ExpenseIqApplication.java          # Main application entry point
│   │
│   ├── config/                             # Configuration classes
│   │   ├── CorsConfig.java                # Cross-Origin Resource Sharing
│   │   ├── DataInitializer.java          # Initialize default data
│   │   ├── JpaConfig.java                # Database configuration
│   │   ├── OpenApiConfig.java            # Swagger documentation
│   │   └── SecurityConfig.java           # Security & authentication
│   │
│   ├── controller/                         # REST API endpoints
│   │   ├── AccountController.java        # /api/accounts
│   │   ├── AuthController.java           # /api/auth
│   │   ├── BudgetController.java         # /api/budgets
│   │   ├── CategoryController.java       # /api/categories
│   │   ├── GoalController.java           # /api/goals
│   │   └── TransactionController.java    # /api/transactions
│   │
│   ├── dto/                                # Data Transfer Objects
│   │   ├── request/                       # Request DTOs (from client)
│   │   │   ├── AccountRequest.java
│   │   │   ├── BudgetRequest.java
│   │   │   ├── CategoryRequest.java
│   │   │   ├── ContributeGoalRequest.java
│   │   │   ├── GoalRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   └── TransactionRequest.java
│   │   │
│   │   └── response/                      # Response DTOs (to client)
│   │       ├── AccountResponse.java
│   │       ├── ApiResponse.java
│   │       ├── AuthResponse.java
│   │       ├── BudgetResponse.java
│   │       ├── CategoryResponse.java
│   │       ├── GoalResponse.java
│   │       ├── TransactionResponse.java
│   │       └── UserResponse.java
│   │
│   ├── entity/                             # Database entities (tables)
│   │   ├── Account.java                  # accounts table
│   │   ├── BaseEntity.java               # Common fields (id, timestamps)
│   │   ├── Budget.java                   # budgets table
│   │   ├── Category.java                 # categories table
│   │   ├── Goal.java                     # goals table
│   │   ├── Notification.java             # notifications table
│   │   ├── RecurringTransaction.java     # recurring_transactions table
│   │   ├── Transaction.java              # transactions table
│   │   └── User.java                     # users table
│   │
│   ├── enums/                              # Enumeration types
│   │   ├── AccountType.java              # CASH, BANK, CREDIT_CARD, etc.
│   │   ├── Frequency.java                # DAILY, WEEKLY, MONTHLY, YEARLY
│   │   ├── NotificationType.java         # Alert types
│   │   ├── Role.java                     # USER, ADMIN
│   │   └── TransactionType.java          # INCOME, EXPENSE
│   │
│   ├── exception/                          # Error handling
│   │   ├── BadRequestException.java      # 400 errors
│   │   ├── DuplicateResourceException.java # 409 errors
│   │   ├── FileStorageException.java     # File upload errors
│   │   ├── GlobalExceptionHandler.java   # Centralized error handling
│   │   ├── ResourceNotFoundException.java # 404 errors
│   │   └── UnauthorizedException.java    # 401 errors
│   │
│   ├── repository/                         # Database access layer
│   │   ├── AccountRepository.java
│   │   ├── BudgetRepository.java
│   │   ├── CategoryRepository.java
│   │   ├── GoalRepository.java
│   │   ├── NotificationRepository.java
│   │   ├── RecurringTransactionRepository.java
│   │   ├── TransactionRepository.java
│   │   └── UserRepository.java
│   │
│   ├── security/                           # Authentication & Security
│   │   ├── JwtAuthenticationEntryPoint.java  # Handle unauthorized access
│   │   ├── JwtAuthenticationFilter.java      # Validate JWT tokens
│   │   ├── JwtTokenProvider.java             # Generate/validate tokens
│   │   ├── SecurityUser.java                 # User details for Spring Security
│   │   └── UserDetailsServiceImpl.java       # Load user from database
│   │
│   └── service/                            # Business logic layer
│       ├── AccountService.java           # Interface
│       ├── AuthService.java              # Interface
│       ├── BudgetService.java            # Interface
│       ├── CategoryService.java          # Interface
│       ├── GoalService.java              # Interface
│       ├── TransactionService.java       # Interface
│       │
│       └── impl/                          # Implementations
│           ├── AccountServiceImpl.java
│           ├── AuthServiceImpl.java
│           ├── BudgetServiceImpl.java
│           ├── CategoryServiceImpl.java
│           ├── GoalServiceImpl.java
│           └── TransactionServiceImpl.java
│
├── src/main/resources/
│   ├── application.yml                    # Main configuration
│   ├── application-dev.yml               # Development profile (H2)
│   ├── application-postgres.yml          # PostgreSQL profile
│   └── application-prod.yml              # Production profile
│
├── pom.xml                                # Maven dependencies
│
└── instructions/                          # This documentation folder
    ├── 01-PROJECT-OVERVIEW.md            # This file
    ├── 02-CONFIGURATION-FILES.md         # Config explanations
    ├── 03-SECURITY-LAYER.md              # Security & JWT
    ├── 04-ENTITIES-EXPLAINED.md          # Database tables
    ├── 05-REPOSITORIES-EXPLAINED.md      # Data access
    ├── 06-SERVICES-EXPLAINED.md          # Business logic
    ├── 07-CONTROLLERS-EXPLAINED.md       # API endpoints
    └── 08-DTOS-EXPLAINED.md              # Data transfer objects
```

---

## 🔄 Request Flow Example

### Example: User creates a transaction

```
1. CLIENT (Browser/Mobile App)
   ↓
   POST /api/transactions
   Headers: Authorization: Bearer <JWT_TOKEN>
   Body: {
     "type": "EXPENSE",
     "amount": 50.00,
     "categoryId": 1,
     "accountId": 1,
     "description": "Lunch"
   }

2. SECURITY FILTER (JwtAuthenticationFilter)
   ↓
   - Extracts JWT token from header
   - Validates token
   - Gets user ID from token
   - Sets authentication in SecurityContext

3. CONTROLLER (TransactionController)
   ↓
   - Receives HTTP request
   - Validates request body
   - Calls service layer
   @PostMapping
   public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(...)

4. SERVICE (TransactionServiceImpl)
   ↓
   - Contains business logic
   - Validates category and account exist
   - Creates transaction entity
   - Updates account balance
   - Saves to database via repository

5. REPOSITORY (TransactionRepository)
   ↓
   - Extends JpaRepository
   - Provides database operations
   - Saves transaction to PostgreSQL

6. DATABASE (PostgreSQL)
   ↓
   - Stores transaction data
   - Maintains relationships (foreign keys)

7. RESPONSE FLOW (Reverse)
   ↓
   Database → Repository → Service → Controller → Client
   
   Returns:
   {
     "success": true,
     "message": "Transaction created successfully",
     "data": {
       "id": 1,
       "type": "EXPENSE",
       "amount": 50.00,
       ...
     }
   }
```

---

## 🎯 Core Features Implemented

### 1. **Authentication & Authorization**
- User registration with email validation
- Login with JWT token generation
- Token refresh mechanism
- Password encryption with BCrypt
- Role-based access control

### 2. **Transaction Management**
- Create, read, update, delete transactions
- Filter by date, category, account, type
- Pagination and sorting
- Transaction summaries (income, expense, balance)
- Recent transactions view

### 3. **Category Management**
- 19 default categories (6 income, 13 expense)
- Custom category creation
- Category icons and colors
- Cannot delete categories with transactions

### 4. **Account Management**
- Multiple accounts (Cash, Bank, Credit Card, Savings, Investment)
- Automatic balance tracking
- Credit limit for credit cards
- Account-wise transaction filtering

### 5. **Budget Management**
- Monthly budgets per category
- Real-time spent calculation
- Budget vs actual comparison
- Percentage used tracking
- Budget alerts

### 6. **Goals & Savings**
- Create savings goals
- Track progress
- Contribute to goals
- Automatic completion detection
- Progress percentage

---

## 🔐 Security Features

1. **JWT Authentication**
   - Stateless authentication
   - Token expiration (24 hours)
   - Refresh token (7 days)

2. **Password Security**
   - BCrypt hashing
   - Salt generation
   - Strong password validation

3. **API Security**
   - All endpoints protected except auth
   - CORS configuration
   - SQL injection prevention (JPA)
   - XSS protection

---

## 📊 Database Schema

### **8 Main Tables:**

1. **users** - User accounts
2. **categories** - Transaction categories
3. **accounts** - User financial accounts
4. **transactions** - Income/expense records
5. **budgets** - Monthly budget limits
6. **goals** - Savings goals
7. **recurring_transactions** - Recurring payments
8. **notifications** - User notifications

### **Relationships:**
- User → has many → Transactions, Accounts, Budgets, Goals
- Transaction → belongs to → User, Category, Account
- Budget → belongs to → User, Category
- Goal → belongs to → User

---

## 🚀 API Endpoints Summary

### **Authentication (3 endpoints)**
- POST `/api/auth/register` - Register new user
- POST `/api/auth/login` - Login user
- POST `/api/auth/refresh-token` - Refresh JWT token

### **Transactions (7 endpoints)**
- POST `/api/transactions` - Create transaction
- GET `/api/transactions` - List with filters
- GET `/api/transactions/{id}` - Get single
- PUT `/api/transactions/{id}` - Update
- DELETE `/api/transactions/{id}` - Delete
- GET `/api/transactions/summary` - Get totals
- GET `/api/transactions/recent` - Recent transactions

### **Categories (5 endpoints)**
- GET `/api/categories` - List all
- POST `/api/categories` - Create custom
- GET `/api/categories/{id}` - Get single
- PUT `/api/categories/{id}` - Update
- DELETE `/api/categories/{id}` - Delete

### **Accounts (5 endpoints)**
- GET `/api/accounts` - List all
- POST `/api/accounts` - Create
- GET `/api/accounts/{id}` - Get single
- PUT `/api/accounts/{id}` - Update
- DELETE `/api/accounts/{id}` - Delete

### **Budgets (5 endpoints)**
- GET `/api/budgets` - List by month
- POST `/api/budgets` - Create
- GET `/api/budgets/{id}` - Get single
- PUT `/api/budgets/{id}` - Update
- DELETE `/api/budgets/{id}` - Delete

### **Goals (6 endpoints)**
- GET `/api/goals` - List all
- POST `/api/goals` - Create
- GET `/api/goals/{id}` - Get single
- PUT `/api/goals/{id}` - Update
- DELETE `/api/goals/{id}` - Delete
- POST `/api/goals/{id}/contribute` - Add money

**Total: 31 API Endpoints**

---

## 🎓 Key Concepts

### **1. Layered Architecture**
- **Controller Layer:** Handles HTTP requests/responses
- **Service Layer:** Contains business logic
- **Repository Layer:** Database access
- **Entity Layer:** Database table representation

### **2. Dependency Injection**
Spring automatically creates and injects dependencies:
```java
@RequiredArgsConstructor  // Lombok generates constructor
public class TransactionService {
    private final TransactionRepository repository;  // Auto-injected
}
```

### **3. JPA (Java Persistence API)**
Maps Java objects to database tables:
```java
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
```

### **4. DTOs (Data Transfer Objects)**
Separate objects for API communication:
- **Request DTOs:** Data coming from client
- **Response DTOs:** Data sent to client
- Prevents exposing internal entity structure

### **5. Exception Handling**
Centralized error handling:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(...)
}
```

---

## 📖 Next Steps

Read the following files in order:
1. ✅ **01-PROJECT-OVERVIEW.md** (You are here)
2. 📄 **02-CONFIGURATION-FILES.md** - Understand configuration
3. 🔐 **03-SECURITY-LAYER.md** - Learn security & JWT
4. 🗄️ **04-ENTITIES-EXPLAINED.md** - Database tables
5. 📊 **05-REPOSITORIES-EXPLAINED.md** - Data access
6. 💼 **06-SERVICES-EXPLAINED.md** - Business logic
7. 🌐 **07-CONTROLLERS-EXPLAINED.md** - API endpoints
8. 📦 **08-DTOS-EXPLAINED.md** - Data transfer objects

---

**Ready to dive deeper? Start with 02-CONFIGURATION-FILES.md!** 🚀
