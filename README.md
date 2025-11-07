# 💰 ExpenseIQ Backend

Personal Finance Manager - REST API built with Spring Boot

## 🚀 Tech Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: MySQL (Production) / H2 (Development)
- **Security**: Spring Security + JWT
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ (for production)

## 🛠️ Setup Instructions

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd expense-iq-backend
```

### 2. Configure Database (Optional for dev)
For development, the app uses H2 in-memory database by default.

For production with MySQL:
```yaml
# Update application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/expenseiq
    username: your_username
    password: your_password
```

### 3. Run the application
```bash
# Development mode (H2 database)
mvn spring-boot:run

# Or with Maven wrapper
./mvnw spring-boot:run
```

The API will start on `http://localhost:8080`

### 4. Access H2 Console (Development only)
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:expenseiq`
- Username: `sa`
- Password: (leave empty)

### 5. Access API Documentation
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/api-docs`

## 📁 Project Structure

```
src/main/java/com/expenseiq/
├── config/              # Configuration classes
├── controller/          # REST Controllers
├── dto/                 # Data Transfer Objects
├── entity/              # JPA Entities
├── repository/          # JPA Repositories
├── service/             # Business Logic
├── security/            # Security & JWT
├── exception/           # Exception Handling
├── enums/               # Enums
└── util/                # Utility classes
```

## 🔐 Authentication

The API uses JWT (JSON Web Tokens) for authentication.

### Register a new user
```bash
POST /api/auth/register
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

### Login
```bash
POST /api/auth/login
{
  "email": "john@example.com",
  "password": "password123"
}
```

Response includes JWT token:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

### Use token in requests
```bash
Authorization: Bearer <your-token>
```

## 📊 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh-token` - Refresh JWT token

### Transactions
- `GET /api/transactions` - List all transactions (with filters)
- `GET /api/transactions/{id}` - Get single transaction
- `POST /api/transactions` - Create transaction
- `PUT /api/transactions/{id}` - Update transaction
- `DELETE /api/transactions/{id}` - Delete transaction

### Categories
- `GET /api/categories` - List all categories
- `POST /api/categories` - Create custom category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

### Accounts
- `GET /api/accounts` - List all accounts
- `POST /api/accounts` - Create account
- `PUT /api/accounts/{id}` - Update account
- `DELETE /api/accounts/{id}` - Delete account

### Budgets
- `GET /api/budgets` - List budgets
- `POST /api/budgets` - Set budget
- `GET /api/budgets/progress` - Get budget progress

### Goals
- `GET /api/goals` - List goals
- `POST /api/goals` - Create goal
- `POST /api/goals/{id}/contribute` - Add money to goal

### Reports
- `GET /api/reports/dashboard` - Dashboard summary
- `GET /api/reports/monthly-trend` - Monthly trends
- `GET /api/reports/category-breakdown` - Category breakdown

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## 📦 Build for Production

```bash
# Create JAR file
mvn clean package

# Run the JAR
java -jar target/expense-iq-backend-1.0.0.jar
```

## 🔧 Environment Variables

```bash
# JWT Secret (required in production)
JWT_SECRET=your-super-secret-key-here

# Database (production)
DATABASE_URL=jdbc:mysql://localhost:3306/expenseiq
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
```

## 📝 Development Roadmap

- [x] Project setup
- [x] Database entities
- [ ] JWT authentication
- [ ] Transaction CRUD
- [ ] Category management
- [ ] Budget tracking
- [ ] Reports & analytics
- [ ] File upload (receipts)
- [ ] Recurring transactions
- [ ] Notifications
- [ ] Email service
- [ ] Unit tests
- [ ] Integration tests

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👨‍💻 Author

Built with ❤️ by [Your Name]
