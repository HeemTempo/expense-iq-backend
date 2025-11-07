# 📚 ExpenseIQ Backend - Complete Documentation

## 🎯 Welcome!

This folder contains **complete line-by-line explanations** of every file in the ExpenseIQ backend project.

---

## 📖 Documentation Files

### **Read in This Order:**

1. **[01-PROJECT-OVERVIEW.md](01-PROJECT-OVERVIEW.md)** ⭐ START HERE
   - What the project does
   - Architecture overview
   - Project structure
   - Request flow
   - Core features

2. **[02-CONFIGURATION-FILES.md](02-CONFIGURATION-FILES.md)**
   - `pom.xml` - Dependencies
   - `application.yml` - Main config
   - `application-postgres.yml` - Database config
   - All configuration classes explained line by line

3. **[03-SECURITY-LAYER.md](03-SECURITY-LAYER.md)**
   - JWT token generation and validation
   - Password hashing with BCrypt
   - Authentication filters
   - Security configuration
   - Complete authentication flow

4. **[04-ENTITIES-EXPLAINED.md](04-ENTITIES-EXPLAINED.md)** (Coming soon)
   - Database tables
   - Entity relationships
   - JPA annotations
   - Field explanations

5. **[05-REPOSITORIES-EXPLAINED.md](05-REPOSITORIES-EXPLAINED.md)** (Coming soon)
   - Data access layer
   - Custom queries
   - JPA methods
   - Query examples

6. **[06-SERVICES-EXPLAINED.md](06-SERVICES-EXPLAINED.md)** (Coming soon)
   - Business logic
   - Service implementations
   - Transaction management
   - Validation rules

7. **[07-CONTROLLERS-EXPLAINED.md](07-CONTROLLERS-EXPLAINED.md)** (Coming soon)
   - REST API endpoints
   - Request handling
   - Response formatting
   - HTTP methods

8. **[08-DTOS-EXPLAINED.md](08-DTOS-EXPLAINED.md)** (Coming soon)
   - Request DTOs
   - Response DTOs
   - Validation annotations
   - DTO patterns

---

## 🎓 What You'll Learn

### **After reading these files, you'll understand:**

✅ How Spring Boot applications work  
✅ How JWT authentication is implemented  
✅ How to design REST APIs  
✅ How JPA/Hibernate maps objects to database  
✅ How to structure a layered architecture  
✅ How dependency injection works  
✅ How to handle errors globally  
✅ How to document APIs with Swagger  
✅ How to configure CORS for frontend  
✅ How to secure endpoints with Spring Security  

---

## 🔍 Quick Reference

### **Find Information About:**

| Topic | File |
|-------|------|
| Project structure | 01-PROJECT-OVERVIEW.md |
| Dependencies | 02-CONFIGURATION-FILES.md |
| Database connection | 02-CONFIGURATION-FILES.md |
| JWT tokens | 03-SECURITY-LAYER.md |
| Password hashing | 03-SECURITY-LAYER.md |
| Authentication flow | 03-SECURITY-LAYER.md |
| Database tables | 04-ENTITIES-EXPLAINED.md |
| Custom queries | 05-REPOSITORIES-EXPLAINED.md |
| Business logic | 06-SERVICES-EXPLAINED.md |
| API endpoints | 07-CONTROLLERS-EXPLAINED.md |
| Request/Response format | 08-DTOS-EXPLAINED.md |

---

## 💡 How to Use This Documentation

### **For Beginners:**
1. Read files in order (1 → 8)
2. Take your time with each file
3. Try to understand each line
4. Refer back when coding

### **For Experienced Developers:**
1. Jump to specific topics
2. Use as reference guide
3. Focus on architecture decisions
4. Learn Spring Boot patterns

### **For Learning:**
1. Read explanation
2. Look at actual code file
3. Compare and understand
4. Try modifying code
5. See what breaks/works

---

## 🎯 Key Concepts Covered

### **1. Layered Architecture**
```
Controller → Service → Repository → Database
```
Each layer has specific responsibility.

### **2. Dependency Injection**
```java
@RequiredArgsConstructor
public class MyService {
    private final MyRepository repository;  // Auto-injected
}
```
Spring creates and injects dependencies automatically.

### **3. REST API Design**
```
GET    /api/resource      - List all
POST   /api/resource      - Create
GET    /api/resource/{id} - Get one
PUT    /api/resource/{id} - Update
DELETE /api/resource/{id} - Delete
```

### **4. JWT Authentication**
```
Client → Login → Server generates JWT → Client stores token
Client → Request + JWT → Server validates → Response
```

### **5. JPA/Hibernate**
```java
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
}
```
Java objects automatically map to database tables.

---

## 📊 Project Statistics

- **Total Files:** 74 Java files
- **Lines of Code:** ~4,600
- **API Endpoints:** 31
- **Database Tables:** 8
- **Controllers:** 6
- **Services:** 6
- **Repositories:** 8
- **Entities:** 9
- **DTOs:** 16

---

## 🚀 Getting Started

### **1. Read the Documentation**
Start with `01-PROJECT-OVERVIEW.md`

### **2. Run the Application**
```bash
cd C:\Users\ANDREW\CascadeProjects\expense-iq-backend
mvn spring-boot:run
```

### **3. Test the API**
Open: http://localhost:8081/swagger-ui.html

### **4. Explore the Code**
Use documentation as guide while reading code.

---

## 🎓 Learning Path

### **Week 1: Basics**
- Read 01-PROJECT-OVERVIEW.md
- Read 02-CONFIGURATION-FILES.md
- Understand project structure
- Run the application

### **Week 2: Security**
- Read 03-SECURITY-LAYER.md
- Understand JWT authentication
- Test login/register endpoints
- Examine security code

### **Week 3: Database**
- Read 04-ENTITIES-EXPLAINED.md
- Read 05-REPOSITORIES-EXPLAINED.md
- Understand JPA/Hibernate
- Examine database schema

### **Week 4: Business Logic**
- Read 06-SERVICES-EXPLAINED.md
- Read 07-CONTROLLERS-EXPLAINED.md
- Read 08-DTOS-EXPLAINED.md
- Build your own feature

---

## 🔗 Additional Resources

### **In Project Root:**
- `README.md` - Project overview
- `GETTING_STARTED.md` - Setup guide
- `API_TESTING_GUIDE.md` - Test endpoints
- `POSTGRESQL_SETUP.md` - Database setup
- `FINAL_IMPLEMENTATION_SUMMARY.md` - Complete summary

### **External Resources:**
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Spring Security Docs](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/) - JWT debugger
- [PostgreSQL Docs](https://www.postgresql.org/docs/)

---

## 💬 Understanding the Code

### **When you see:**

```java
@RestController
```
**This means:** This class handles HTTP requests (REST API endpoint).

```java
@Service
```
**This means:** This class contains business logic.

```java
@Repository
```
**This means:** This class accesses the database.

```java
@Entity
```
**This means:** This class represents a database table.

```java
@Autowired or @RequiredArgsConstructor
```
**This means:** Spring automatically injects dependencies.

```java
@GetMapping("/api/resource")
```
**This means:** This method handles GET requests to `/api/resource`.

```java
@Valid
```
**This means:** Validate request body before processing.

```java
@Transactional
```
**This means:** Database operation (rollback if error).

---

## 🎯 Goals of This Documentation

1. ✅ Explain every file's purpose
2. ✅ Explain every line of code
3. ✅ Show how components connect
4. ✅ Teach Spring Boot concepts
5. ✅ Make code understandable for beginners
6. ✅ Provide reference for experienced developers
7. ✅ Enable independent learning
8. ✅ Support code modifications

---

## 📞 How to Use While Coding

### **Scenario 1: Adding New Feature**
1. Read relevant documentation file
2. Find similar existing feature
3. Copy and modify
4. Test with Swagger

### **Scenario 2: Fixing Bug**
1. Identify which layer has bug
2. Read that layer's documentation
3. Understand the flow
4. Fix and test

### **Scenario 3: Understanding Code**
1. Find file in documentation
2. Read line-by-line explanation
3. Compare with actual code
4. Understand the pattern

---

## 🎉 You're Ready!

Start with **[01-PROJECT-OVERVIEW.md](01-PROJECT-OVERVIEW.md)** and work your way through!

Each file builds on the previous one, creating a complete understanding of the entire backend system.

**Happy Learning!** 🚀📚

---

## 📝 Documentation Status

| File | Status | Description |
|------|--------|-------------|
| README.md | ✅ Complete | This file |
| 01-PROJECT-OVERVIEW.md | ✅ Complete | Project structure & overview |
| 02-CONFIGURATION-FILES.md | ✅ Complete | All config files explained |
| 03-SECURITY-LAYER.md | ✅ Complete | Authentication & JWT |
| 04-ENTITIES-EXPLAINED.md | 🔄 In Progress | Database entities |
| 05-REPOSITORIES-EXPLAINED.md | 📋 Planned | Data access layer |
| 06-SERVICES-EXPLAINED.md | 📋 Planned | Business logic |
| 07-CONTROLLERS-EXPLAINED.md | 📋 Planned | REST endpoints |
| 08-DTOS-EXPLAINED.md | 📋 Planned | Data transfer objects |

---

**Last Updated:** November 7, 2025  
**Version:** 1.0.0  
**Project:** ExpenseIQ Backend
