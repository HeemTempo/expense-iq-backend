# 🧪 ExpenseIQ API Testing Guide

Complete guide to test all implemented endpoints using Postman, cURL, or Swagger.

---

## 🚀 Getting Started

### 1. Start the Application
```bash
cd C:\Users\ANDREW\CascadeProjects\expense-iq-backend
mvn spring-boot:run
```

### 2. Access Swagger UI
this code do 

### 3. Base URL
```
http://localhost:8081/api
```

---

## 🔐 Authentication Endpoints

### 1. Register a New User
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "currency": "USD"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "expiresIn": 86400000,
    "user": {
      "id": 1,
      "email": "john@example.com",
      "name": "John Doe",
      "profilePicture": null,
      "currency": "USD"
    }
  }
}
```

### 2. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

### 3. Refresh Token
```http
POST /api/auth/refresh-token
Content-Type: application/json

"your-refresh-token-here"
```

---

## 💳 Transaction Endpoints

**Note:** All transaction endpoints require authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-token>
```

### 1. Create Transaction
```http
POST /api/transactions
Authorization: Bearer <token>
Content-Type: application/json

{
  "type": "EXPENSE",
  "amount": 50.00,
  "categoryId": 1,
  "accountId": 1,
  "description": "Lunch at restaurant",
  "date": "2024-12-25",
  "isRecurring": false
}
```

### 2. Get All Transactions (with filters)
```http
GET /api/transactions?page=0&size=10&sort=date,desc
GET /api/transactions?type=EXPENSE&startDate=2024-01-01&endDate=2024-12-31
GET /api/transactions?categoryId=1&accountId=1
GET /api/transactions?description=lunch
```

### 3. Get Transaction by ID
```http
GET /api/transactions/1
```

### 4. Update Transaction
```http
PUT /api/transactions/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "type": "EXPENSE",
  "amount": 55.00,
  "categoryId": 1,
  "accountId": 1,
  "description": "Lunch at restaurant (updated)",
  "date": "2024-12-25",
  "isRecurring": false
}
```

### 5. Delete Transaction
```http
DELETE /api/transactions/1
```

### 6. Get Transaction Summary
```http
GET /api/transactions/summary
GET /api/transactions/summary?startDate=2024-01-01&endDate=2024-12-31
```

**Response:**
```json
{
  "success": true,
  "message": "Transaction summary retrieved successfully",
  "data": {
    "income": 5000.00,
    "expense": 3200.00,
    "balance": 1800.00
  }
}
```

### 7. Get Recent Transactions
```http
GET /api/transactions/recent?limit=5
```

---

## 🏷️ Category Endpoints

### 1. Get All Categories
```http
GET /api/categories
GET /api/categories?type=EXPENSE
GET /api/categories?type=INCOME
```

### 2. Create Custom Category
```http
POST /api/categories
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Gym Membership",
  "type": "EXPENSE",
  "icon": "🏋️",
  "color": "#3B82F6"
}
```

### 3. Update Category
```http
PUT /api/categories/20
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Fitness",
  "type": "EXPENSE",
  "icon": "💪",
  "color": "#3B82F6"
}
```

### 4. Delete Category
```http
DELETE /api/categories/20
```

**Note:** Cannot delete default categories or categories with existing transactions.

---

## 💼 Account Endpoints

### 1. Create Account
```http
POST /api/accounts
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Main Bank Account",
  "type": "BANK",
  "balance": 5000.00,
  "creditLimit": null
}
```

### 2. Get All Accounts
```http
GET /api/accounts
```

### 3. Get Account by ID
```http
GET /api/accounts/1
```

### 4. Update Account
```http
PUT /api/accounts/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Chase Checking",
  "type": "BANK",
  "balance": 5500.00,
  "creditLimit": null
}
```

### 5. Delete Account
```http
DELETE /api/accounts/1
```

---

## 🎯 Budget Endpoints

### 1. Create Budget
```http
POST /api/budgets
Authorization: Bearer <token>
Content-Type: application/json

{
  "categoryId": 1,
  "amount": 500.00,
  "month": 12,
  "year": 2024
}
```

### 2. Get Budgets for Month
```http
GET /api/budgets?month=12&year=2024
GET /api/budgets  (defaults to current month)
```

**Response:**
```json
{
  "success": true,
  "message": "Budgets retrieved successfully",
  "data": [
    {
      "id": 1,
      "category": {
        "id": 1,
        "name": "Food & Dining",
        "type": "EXPENSE",
        "icon": "🍕",
        "color": "#EF4444",
        "isDefault": true
      },
      "amount": 500.00,
      "month": 12,
      "year": 2024,
      "spent": 420.00,
      "remaining": 80.00,
      "percentageUsed": 84.0
    }
  ]
}
```

### 3. Get Budget Progress
```http
GET /api/budgets/progress?month=12&year=2024
```

### 4. Update Budget
```http
PUT /api/budgets/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "categoryId": 1,
  "amount": 600.00,
  "month": 12,
  "year": 2024
}
```

### 5. Delete Budget
```http
DELETE /api/budgets/1
```

---

## 💰 Goal Endpoints

### 1. Create Goal
```http
POST /api/goals
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Vacation Fund",
  "targetAmount": 3000.00,
  "deadline": "2025-06-01",
  "icon": "🏖️"
}
```

### 2. Get All Goals
```http
GET /api/goals
GET /api/goals?activeOnly=true  (only incomplete goals)
```

**Response:**
```json
{
  "success": true,
  "message": "Goals retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Vacation Fund",
      "targetAmount": 3000.00,
      "currentAmount": 1200.00,
      "deadline": "2025-06-01",
      "icon": "🏖️",
      "completed": false,
      "progressPercentage": 40.0
    }
  ]
}
```

### 3. Contribute to Goal
```http
POST /api/goals/1/contribute
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": 100.00
}
```

### 4. Update Goal
```http
PUT /api/goals/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Summer Vacation",
  "targetAmount": 3500.00,
  "deadline": "2025-07-01",
  "icon": "🏖️"
}
```

### 5. Delete Goal
```http
DELETE /api/goals/1
```

---

## 📊 Testing Workflow

### Complete Test Scenario:

1. **Register a user**
   ```
   POST /api/auth/register
   ```

2. **Login and get token**
   ```
   POST /api/auth/login
   ```

3. **View default categories**
   ```
   GET /api/categories
   ```

4. **Create an account**
   ```
   POST /api/accounts
   {
     "name": "Cash",
     "type": "CASH",
     "balance": 1000.00
   }
   ```

5. **Create income transaction**
   ```
   POST /api/transactions
   {
     "type": "INCOME",
     "amount": 5000.00,
     "categoryId": 1,  (Salary category)
     "accountId": 1,
     "description": "Monthly salary",
     "date": "2024-12-01"
   }
   ```

6. **Create expense transaction**
   ```
   POST /api/transactions
   {
     "type": "EXPENSE",
     "amount": 50.00,
     "categoryId": 7,  (Food & Dining)
     "accountId": 1,
     "description": "Lunch",
     "date": "2024-12-25"
   }
   ```

7. **Check transaction summary**
   ```
   GET /api/transactions/summary
   ```

8. **Set a budget**
   ```
   POST /api/budgets
   {
     "categoryId": 7,
     "amount": 500.00,
     "month": 12,
     "year": 2024
   }
   ```

9. **Check budget progress**
   ```
   GET /api/budgets/progress
   ```

10. **Create a savings goal**
    ```
    POST /api/goals
    {
      "name": "Emergency Fund",
      "targetAmount": 10000.00,
      "deadline": "2025-12-31",
      "icon": "🚨"
    }
    ```

11. **Contribute to goal**
    ```
    POST /api/goals/1/contribute
    {
      "amount": 500.00
    }
    ```

---

## 🔍 H2 Database Console

Access the H2 console to view data directly:

**URL:** `http://localhost:8080/h2-console`

**Connection Details:**
- JDBC URL: `jdbc:h2:mem:expenseiq`
- Username: `sa`
- Password: (leave empty)

**Useful SQL Queries:**
```sql
-- View all users
SELECT * FROM users;

-- View all transactions
SELECT * FROM transactions;

-- View all categories
SELECT * FROM categories;

-- View transaction summary
SELECT type, SUM(amount) as total 
FROM transactions 
WHERE user_id = 1 
GROUP BY type;

-- View budget vs spent
SELECT 
    b.id,
    c.name as category,
    b.amount as budget,
    COALESCE(SUM(t.amount), 0) as spent
FROM budgets b
LEFT JOIN categories c ON b.category_id = c.id
LEFT JOIN transactions t ON t.category_id = c.id 
    AND t.user_id = b.user_id
    AND MONTH(t.date) = b.month
    AND YEAR(t.date) = b.year
WHERE b.user_id = 1
GROUP BY b.id, c.name, b.amount;
```

---

## ✅ Expected Responses

### Success Response Format:
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

### Error Response Format:
```json
{
  "status": 400,
  "message": "Error message",
  "timestamp": "2024-12-25T10:30:00"
}
```

### Validation Error Format:
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "email": "Email should be valid",
    "password": "Password must be at least 6 characters"
  },
  "timestamp": "2024-12-25T10:30:00"
}
```

---

## 🎯 Status Codes

- `200 OK` - Successful GET, PUT, DELETE
- `201 Created` - Successful POST
- `400 Bad Request` - Validation error
- `401 Unauthorized` - Missing or invalid token
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate resource
- `500 Internal Server Error` - Server error

---

## 💡 Tips

1. **Save your token** after login/register for subsequent requests
2. **Use Postman Collections** to organize your tests
3. **Test filters** with different combinations
4. **Check H2 Console** to verify data changes
5. **Test error cases** (invalid data, missing fields, etc.)
6. **Test pagination** with different page sizes
7. **Test date ranges** for transactions and summaries

---

## 🚀 Next: Frontend Integration

Once all endpoints are tested and working, you can:
1. Start building the React frontend
2. Connect to these APIs using Axios
3. Implement the UI components
4. Add charts and visualizations

**The backend is ready for production use!** 🎉
