# 🐘 PostgreSQL Setup Guide for ExpenseIQ

## Prerequisites

### 1. Install PostgreSQL
Download and install PostgreSQL from: https://www.postgresql.org/download/windows/

**During installation:**
- Set password for `postgres` user (remember this!)
- Default port: `5432`
- Remember the installation directory

### 2. Verify PostgreSQL is Running
```bash
# Check if PostgreSQL service is running
psql --version
```

---

## 🗄️ Database Setup

### Option 1: Using pgAdmin (GUI)
1. Open **pgAdmin** (installed with PostgreSQL)
2. Connect to PostgreSQL server
3. Right-click on "Databases" → Create → Database
4. Database name: `expenseiq`
5. Click "Save"

### Option 2: Using Command Line
```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE expenseiq;

# Verify
\l

# Exit
\q
```

---

## ⚙️ Application Configuration

### For Development (Using PostgreSQL)

**Option 1: Use the postgres profile**

Update `application.yml` line 6:
```yaml
spring:
  profiles:
    active: postgres  # Changed from 'dev'
```

**Option 2: Keep H2 for development**

If you want to keep H2 for quick testing and use PostgreSQL for production, keep:
```yaml
spring:
  profiles:
    active: dev  # Uses H2 in-memory
```

---

## 🚀 Running the Application

### With PostgreSQL Profile
```bash
# Make sure PostgreSQL is running
# Make sure database 'expenseiq' exists

# Run with postgres profile
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Or change application.yml
```yaml
spring:
  profiles:
    active: postgres
```

Then run:
```bash
mvn spring-boot:run
```

---

## 🔧 Configuration Files

### Current Setup:

1. **application.yml** - Main config (PostgreSQL dialect)
2. **application-dev.yml** - H2 in-memory (for quick testing)
3. **application-postgres.yml** - PostgreSQL local development
4. **application-prod.yml** - PostgreSQL production

---

## 📝 Connection Details

### Development (application-postgres.yml)
```
URL: jdbc:postgresql://localhost:5432/expenseiq
Username: postgres
Password: postgres
```

### Production (application-prod.yml)
```
URL: ${DATABASE_URL:jdbc:postgresql://localhost:5432/expenseiq}
Username: ${DATABASE_USERNAME:postgres}
Password: ${DATABASE_PASSWORD:password}
```

**Note:** In production, set these as environment variables!

---

## 🧪 Testing the Connection

### 1. Start PostgreSQL
Make sure PostgreSQL service is running.

### 2. Create Database
```sql
CREATE DATABASE expenseiq;
```

### 3. Update Password (if needed)
Edit `application-postgres.yml`:
```yaml
spring:
  datasource:
    password: YOUR_POSTGRES_PASSWORD  # Change this
```

### 4. Run Application
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

### 5. Check Logs
You should see:
```
Hibernate: create table users (...)
Hibernate: create table transactions (...)
...
Started ExpenseIqApplication in X seconds
```

---

## 🔍 Verify Database

### Using psql
```bash
# Connect to database
psql -U postgres -d expenseiq

# List tables
\dt

# View users table
SELECT * FROM users;

# Exit
\q
```

### Using pgAdmin
1. Open pgAdmin
2. Navigate to: Servers → PostgreSQL → Databases → expenseiq → Schemas → public → Tables
3. Right-click on a table → View/Edit Data

---

## 🎯 Quick Start Commands

### 1. Create Database
```bash
psql -U postgres -c "CREATE DATABASE expenseiq;"
```

### 2. Run Application
```bash
cd C:\Users\ANDREW\CascadeProjects\expense-iq-backend
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

### 3. Access Application
- **API:** http://localhost:8081/api
- **Swagger:** http://localhost:8081/swagger-ui.html

---

## 🐛 Troubleshooting

### Error: "database does not exist"
**Solution:** Create the database first
```bash
psql -U postgres -c "CREATE DATABASE expenseiq;"
```

### Error: "password authentication failed"
**Solution:** Update password in `application-postgres.yml`

### Error: "Connection refused"
**Solution:** Make sure PostgreSQL service is running
```bash
# Windows - Check service
services.msc
# Look for "postgresql-x64-XX"
```

### Error: Port 5432 already in use
**Solution:** Another PostgreSQL instance is running or port is occupied

---

## 📊 Database Schema

The application will automatically create these tables:
- `users` - User accounts
- `categories` - Transaction categories (19 default)
- `accounts` - User accounts (bank, cash, etc.)
- `transactions` - Income/expense transactions
- `budgets` - Monthly budgets
- `goals` - Savings goals
- `recurring_transactions` - Recurring transactions
- `notifications` - User notifications

---

## 🔄 Switching Between Databases

### Use H2 (In-Memory) - Fast Testing
```yaml
# application.yml
spring:
  profiles:
    active: dev
```

### Use PostgreSQL - Persistent Data
```yaml
# application.yml
spring:
  profiles:
    active: postgres
```

---

## 🎉 You're Ready!

Once PostgreSQL is set up and running:

1. ✅ Database created: `expenseiq`
2. ✅ Application configured for PostgreSQL
3. ✅ Run: `mvn spring-boot:run -Dspring-boot.run.profiles=postgres`
4. ✅ Test: Register a user and create transactions
5. ✅ Data persists even after restart!

**Enjoy your PostgreSQL-powered ExpenseIQ backend!** 🚀
