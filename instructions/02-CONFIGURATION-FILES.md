# ⚙️ Configuration Files Explained - Line by Line

## 📄 File: `pom.xml`

**What it does:** Maven configuration file that manages project dependencies and build settings.

### **Key Sections Explained:**

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>
```
**This does:** Inherits Spring Boot's default configuration and dependency versions.

```xml
<properties>
    <java.version>17</java.version>
</properties>
```
**This does:** Sets Java version to 17 for compilation.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
**This does:** Adds Spring MVC for building REST APIs (includes Tomcat server).

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```
**This does:** Adds JPA/Hibernate for database operations.

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```
**This does:** PostgreSQL database driver for connecting to PostgreSQL.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
**This does:** Adds Spring Security for authentication and authorization.

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
```
**This does:** JWT library for creating and validating JSON Web Tokens.

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```
**This does:** Lombok reduces boilerplate code (auto-generates getters, setters, constructors).

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```
**This does:** Adds Swagger UI for API documentation.

---

## 📄 File: `application.yml`

**What it does:** Main configuration file for the Spring Boot application.

### **Line by Line Explanation:**

```yaml
spring:
  application:
    name: expense-iq-backend
```
**This does:** Sets the application name (used in logs and monitoring).

```yaml
  profiles:
    active: postgres
```
**This does:** Activates the "postgres" profile (loads `application-postgres.yml`).

```yaml
  jpa:
    hibernate:
      ddl-auto: update
```
**This does:** 
- `update` = Hibernate automatically updates database schema
- Creates tables if they don't exist
- Adds new columns if entity changes
- **Never deletes** existing data

```yaml
    show-sql: true
```
**This does:** Prints SQL queries to console (helpful for debugging).

```yaml
    properties:
      hibernate:
        format_sql: true
```
**This does:** Formats SQL queries nicely in logs.

```yaml
        dialect: org.hibernate.dialect.PostgreSQLDialect
```
**This does:** Tells Hibernate to use PostgreSQL-specific SQL syntax.

```yaml
  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB
      max-request-size: 10MB
```
**This does:** 
- Enables file uploads
- Max single file size: 10MB
- Max total request size: 10MB

```yaml
server:
  port: 8081
```
**This does:** Application runs on port 8081 (http://localhost:8081).

```yaml
  error:
    include-message: always
    include-binding-errors: always
```
**This does:** Includes detailed error messages in API responses.

```yaml
jwt:
  secret: ${JWT_SECRET:YourSuperSecretKeyForJWTTokenGenerationMustBeLongEnough12345}
```
**This does:** 
- JWT signing key (keep this secret!)
- `${JWT_SECRET:...}` = Use environment variable JWT_SECRET if set, otherwise use default
- **Production:** Always use environment variable!

```yaml
  expiration: 86400000  # 24 hours in milliseconds
```
**This does:** JWT token expires after 24 hours.

```yaml
  refresh-expiration: 604800000  # 7 days
```
**This does:** Refresh token expires after 7 days.

```yaml
file:
  upload-dir: ./uploads
```
**This does:** Directory where uploaded files (receipts) are stored.

```yaml
cors:
  allowed-origins: http://localhost:5173,http://localhost:3000
```
**This does:** Allows requests from these frontend URLs (React/Vue apps).

```yaml
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
```
**This does:** Allows these HTTP methods from frontend.

```yaml
  allowed-headers: "*"
```
**This does:** Allows all headers (including Authorization).

```yaml
  allow-credentials: true
```
**This does:** Allows cookies and authentication headers.

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
```
**This does:** OpenAPI JSON available at `/v3/api-docs`.

```yaml
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
```
**This does:** Swagger UI available at `/swagger-ui.html`.

---

## 📄 File: `application-postgres.yml`

**What it does:** PostgreSQL-specific configuration (loaded when profile=postgres).

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5434/expenseiq
```
**This does:** 
- Connects to PostgreSQL database
- Host: localhost
- Port: 5434
- Database name: expenseiq

```yaml
    username: postgres
    password: 12345
```
**This does:** Database credentials (change in production!).

```yaml
    driver-class-name: org.postgresql.Driver
```
**This does:** PostgreSQL JDBC driver class.

```yaml
  jpa:
    hibernate:
      ddl-auto: create-drop
```
**This does:** 
- `create-drop` = Creates tables on startup, drops on shutdown
- **Development only!** (loses all data on restart)
- Production should use `validate` or `none`

```yaml
    show-sql: true
```
**This does:** Shows SQL queries in console.

```yaml
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```
**This does:** Uses PostgreSQL dialect for SQL generation.

```yaml
logging:
  level:
    com.expenseiq: DEBUG
```
**This does:** Sets log level to DEBUG for our application code.

```yaml
    org.springframework.security: DEBUG
```
**This does:** Shows detailed security logs (authentication, authorization).

---

## 📄 File: `application-prod.yml`

**What it does:** Production environment configuration.

```yaml
spring:
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5434/expenseiq}
```
**This does:** 
- Uses environment variable `DATABASE_URL` if set
- Falls back to localhost if not set
- **Production:** Set DATABASE_URL environment variable!

```yaml
    username: ${DATABASE_USERNAME:postgres}
    password: ${DATABASE_PASSWORD:password}
```
**This does:** 
- Uses environment variables for credentials
- **Never hardcode production passwords!**

```yaml
  jpa:
    hibernate:
      ddl-auto: validate
```
**This does:** 
- `validate` = Only validates schema, doesn't modify database
- **Safe for production** (no accidental data loss)

```yaml
    show-sql: false
```
**This does:** Disables SQL logging in production (performance).

```yaml
logging:
  level:
    com.expenseiq: INFO
```
**This does:** Only logs INFO level and above (less verbose).

```yaml
    org.springframework.security: WARN
```
**This does:** Only logs warnings and errors for security.

---

## 📄 File: `JpaConfig.java`

**Location:** `src/main/java/com/expenseiq/config/JpaConfig.java`

```java
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
```

**Line by Line:**

```java
@Configuration
```
**This does:** Tells Spring this class contains configuration.

```java
@EnableJpaAuditing
```
**This does:** 
- Enables automatic timestamp tracking
- Sets `createdAt` when entity is created
- Updates `updatedAt` when entity is modified
- Works with `@CreatedDate` and `@LastModifiedDate` annotations

---

## 📄 File: `CorsConfig.java`

**Location:** `src/main/java/com/expenseiq/config/CorsConfig.java`

```java
@Configuration
public class CorsConfig {
    
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;
```

**This does:** 
- `@Configuration` = Spring configuration class
- `@Value` = Injects value from `application.yml`
- Reads `cors.allowed-origins` property

```java
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
```

**This does:** 
- `@Bean` = Creates a Spring-managed bean
- `WebMvcConfigurer` = Configures Spring MVC
- `addCorsMappings` = Configures CORS rules

```java
                registry.addMapping("/**")
```
**This does:** Applies CORS to all endpoints (`/**` = all paths).

```java
                        .allowedOrigins(allowedOrigins.split(","))
```
**This does:** 
- Splits comma-separated origins
- Allows requests from these URLs

```java
                        .allowedMethods(allowedMethods.split(","))
```
**This does:** Allows specified HTTP methods (GET, POST, etc.).

```java
                        .allowedHeaders(allowedHeaders)
```
**This does:** Allows specified headers (Authorization, Content-Type, etc.).

```java
                        .allowCredentials(allowCredentials);
```
**This does:** Allows cookies and authentication headers.

---

## 📄 File: `DataInitializer.java`

**Location:** `src/main/java/com/expenseiq/config/DataInitializer.java`

```java
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
```

**This does:** 
- `@Component` = Spring-managed component
- `@RequiredArgsConstructor` = Lombok generates constructor
- `CommandLineRunner` = Runs code after application starts

```java
    private final CategoryService categoryService;
```
**This does:** Injects CategoryService (Spring auto-wires it).

```java
    @Override
    public void run(String... args) {
        categoryService.initializeDefaultCategories();
    }
```
**This does:** 
- Runs automatically on application startup
- Calls `initializeDefaultCategories()`
- Creates 19 default categories if they don't exist

---

## 📄 File: `OpenApiConfig.java`

**Location:** `src/main/java/com/expenseiq/config/OpenApiConfig.java`

```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI expenseIqOpenAPI() {
```
**This does:** Creates Swagger/OpenAPI configuration bean.

```java
        final String securitySchemeName = "bearerAuth";
```
**This does:** Names the security scheme "bearerAuth".

```java
        return new OpenAPI()
                .info(new Info()
                        .title("ExpenseIQ API")
                        .description("Personal Finance Manager - Complete REST API Documentation")
                        .version("1.0.0")
```
**This does:** Sets API documentation title, description, and version.

```java
                        .contact(new Contact()
                                .name("ExpenseIQ Team")
                                .email("support@expenseiq.com"))
```
**This does:** Adds contact information to API docs.

```java
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
```
**This does:** Adds license information.

```java
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
```
**This does:** Requires authentication for all endpoints (except those marked public).

```java
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
```
**This does:** 
- Defines JWT authentication scheme
- Type: HTTP Bearer
- Format: JWT token
- Shows "Authorize" button in Swagger UI

---

## 🎯 Summary

### **Configuration Files Purpose:**

| File | Purpose |
|------|---------|
| `pom.xml` | Maven dependencies and build configuration |
| `application.yml` | Main application settings |
| `application-postgres.yml` | PostgreSQL database connection |
| `application-prod.yml` | Production environment settings |
| `JpaConfig.java` | Enables automatic timestamp tracking |
| `CorsConfig.java` | Allows frontend to call backend APIs |
| `DataInitializer.java` | Creates default categories on startup |
| `OpenApiConfig.java` | Configures Swagger API documentation |

---

## 🔑 Key Takeaways

1. **Profiles** = Different configurations for different environments (dev, prod)
2. **Environment Variables** = Use for sensitive data (passwords, secrets)
3. **CORS** = Required for frontend-backend communication
4. **JPA Auditing** = Automatic timestamp tracking
5. **Swagger** = Interactive API documentation

---

**Next:** Read `03-SECURITY-LAYER.md` to understand authentication and JWT! 🔐
