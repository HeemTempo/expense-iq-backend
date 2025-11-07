# 🔐 Security Layer Explained - Line by Line

## Overview: How Authentication Works

```
1. User registers/logs in
2. Server generates JWT token
3. Client stores token
4. Client sends token with each request
5. Server validates token
6. Server processes request if valid
```

---

## 📄 File: `JwtTokenProvider.java`

**Location:** `src/main/java/com/expenseiq/security/JwtTokenProvider.java`

**What it does:** Creates and validates JWT tokens.

### **Line by Line Explanation:**

```java
@Component
public class JwtTokenProvider {
```
**This does:** `@Component` makes this a Spring-managed bean (auto-created).

```java
    @Value("${jwt.secret}")
    private String jwtSecret;
```
**This does:** 
- Injects JWT secret from `application.yml`
- Used to sign and verify tokens
- **Keep this secret!**

```java
    @Value("${jwt.expiration}")
    private long jwtExpiration;
```
**This does:** Token expiration time (24 hours = 86400000 milliseconds).

```java
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;
```
**This does:** Refresh token expiration (7 days = 604800000 milliseconds).

```java
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
```
**This does:** 
- Converts secret string to cryptographic key
- BASE64 decoding for security
- HMAC-SHA algorithm for signing

```java
    public String generateToken(String email, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
```
**This does:** 
- Gets current time
- Calculates expiration time (now + 24 hours)

```java
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
```
**This does:** 
- `setSubject(email)` = Stores user email in token
- `claim("userId", userId)` = Stores user ID in token
- `setIssuedAt(now)` = When token was created
- `setExpiration(expiryDate)` = When token expires
- `signWith(...)` = Signs token with secret key
- `compact()` = Converts to string (JWT format)

**Result:** Token looks like: `eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJpYXQiOjE2MzA0NTY3ODksImV4cCI6MTYzMDU0MzE4OX0.signature`

```java
    public String generateRefreshToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);
        
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
```
**This does:** Same as `generateToken` but expires in 7 days (for refreshing expired tokens).

```java
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }
```
**This does:** 
- Parses JWT token
- Verifies signature
- Extracts email (subject)
- Throws exception if token is invalid

```java
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.get("userId", Long.class);
    }
```
**This does:** Extracts user ID from token.

```java
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
```
**This does:** 
- Validates token signature
- Checks if token is expired
- Returns `true` if valid, `false` if invalid
- Catches exceptions (expired, tampered, malformed)

---

## 📄 File: `SecurityUser.java`

**Location:** `src/main/java/com/expenseiq/security/SecurityUser.java`

**What it does:** Adapts our `User` entity to Spring Security's `UserDetails` interface.

```java
@Getter
public class SecurityUser implements UserDetails {
```
**This does:** 
- `@Getter` = Lombok generates getters
- `UserDetails` = Spring Security interface for user information

```java
    private final Long id;
    private final String email;
    private final String password;
    private final String name;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;
```
**This does:** Stores user information needed for authentication.

```java
    public SecurityUser(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
        this.enabled = user.getEnabled();
    }
```
**This does:** 
- Constructor converts `User` entity to `SecurityUser`
- Creates authority from role (e.g., "ROLE_USER")
- `Collections.singletonList` = Creates list with one item

```java
    @Override
    public String getUsername() {
        return email;
    }
```
**This does:** Spring Security uses email as username.

```java
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
```
**This does:** 
- Required by `UserDetails` interface
- All return `true` (we don't use these features)
- `isEnabled()` checks if user account is active

---

## 📄 File: `UserDetailsServiceImpl.java`

**Location:** `src/main/java/com/expenseiq/security/UserDetailsServiceImpl.java`

**What it does:** Loads user from database for authentication.

```java
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
```
**This does:** 
- `@Service` = Spring service component
- `UserDetailsService` = Spring Security interface

```java
    private final UserRepository userRepository;
```
**This does:** Injects UserRepository to query database.

```java
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        return new SecurityUser(user);
    }
```
**This does:** 
- Spring Security calls this during login
- Searches database for user by email
- Throws exception if user not found
- Converts `User` to `SecurityUser`
- Spring Security then checks password

---

## 📄 File: `JwtAuthenticationFilter.java`

**Location:** `src/main/java/com/expenseiq/security/JwtAuthenticationFilter.java`

**What it does:** Intercepts every request to validate JWT token.

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
```
**This does:** 
- `OncePerRequestFilter` = Runs once per request
- `@Slf4j` = Lombok adds logging

```java
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
```
**This does:** Injects dependencies for token validation and user loading.

```java
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
```
**This does:** Main filter method called for each request.

```java
        try {
            String jwt = getJwtFromRequest(request);
```
**This does:** Extracts JWT token from request header.

```java
            if (jwt != null && tokenProvider.validateToken(jwt)) {
```
**This does:** Checks if token exists and is valid.

```java
                String email = tokenProvider.getEmailFromToken(jwt);
```
**This does:** Extracts email from token.

```java
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
```
**This does:** Loads user details from database.

```java
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
```
**This does:** 
- Creates authentication object
- `userDetails` = User information
- `null` = No credentials needed (already validated)
- `authorities` = User roles/permissions

```java
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
```
**This does:** Adds request details (IP address, session ID).

```java
                SecurityContextHolder.getContext().setAuthentication(authentication);
```
**This does:** 
- Sets authentication in Spring Security context
- Makes user available throughout request
- Controllers can access via `@AuthenticationPrincipal`

```java
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
```
**This does:** 
- Logs errors
- Continues filter chain (even if authentication fails)
- Next filter or controller handles request

```java
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
```
**This does:** 
- Gets "Authorization" header
- Checks if it starts with "Bearer "
- Removes "Bearer " prefix
- Returns token (e.g., "Bearer eyJhbG..." → "eyJhbG...")

---

## 📄 File: `JwtAuthenticationEntryPoint.java`

**Location:** `src/main/java/com/expenseiq/security/JwtAuthenticationEntryPoint.java`

**What it does:** Handles unauthorized access (401 errors).

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
```
**This does:** Implements Spring Security's entry point for authentication errors.

```java
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
```
**This does:** Called when unauthenticated user tries to access protected endpoint.

```java
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
```
**This does:** 
- Sends HTTP 401 status code
- Message: "Unauthorized"
- Client knows authentication is required

---

## 📄 File: `SecurityConfig.java`

**Location:** `src/main/java/com/expenseiq/config/SecurityConfig.java`

**What it does:** Main Spring Security configuration.

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
```
**This does:** 
- `@Configuration` = Configuration class
- `@EnableWebSecurity` = Enables Spring Security
- `@RequiredArgsConstructor` = Injects dependencies

```java
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
```
**This does:** Injects security components.

```java
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
```
**This does:** 
- Creates password encoder bean
- BCrypt = Strong hashing algorithm
- Automatically salts passwords
- Used for password hashing and verification

```java
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
```
**This does:** 
- Creates authentication provider
- `DaoAuthenticationProvider` = Database authentication
- Sets user details service (loads users)
- Sets password encoder (verifies passwords)

```java
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
```
**This does:** 
- Creates authentication manager bean
- Used in login process
- Coordinates authentication

```java
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
```
**This does:** Main security configuration method.

```java
        http
                .csrf(AbstractHttpConfigurer::disable)
```
**This does:** 
- Disables CSRF protection
- Safe for REST APIs (using JWT)
- CSRF needed only for session-based auth

```java
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
```
**This does:** Uses our custom entry point for 401 errors.

```java
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```
**This does:** 
- `STATELESS` = No sessions
- Each request must have JWT token
- Server doesn't store session data

```java
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
```
**This does:** Allows unauthenticated access to `/api/auth/**` (register, login).

```java
                        .requestMatchers("/h2-console/**").permitAll()
```
**This does:** Allows access to H2 console (development only).

```java
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
```
**This does:** Allows access to Swagger documentation.

```java
                        .anyRequest().authenticated()
```
**This does:** All other requests require authentication.

```java
                );
        
        http.authenticationProvider(authenticationProvider());
```
**This does:** Sets our authentication provider.

```java
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
```
**This does:** 
- Adds JWT filter before standard authentication filter
- JWT filter runs first to validate tokens

```java
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
```
**This does:** Allows H2 console to work (uses frames).

```java
        return http.build();
    }
```
**This does:** Builds and returns security configuration.

---

## 🔄 Authentication Flow

### **Registration Flow:**

```
1. Client sends POST /api/auth/register
   Body: { name, email, password }

2. AuthController receives request

3. AuthService:
   - Checks if email exists
   - Hashes password with BCrypt
   - Saves user to database

4. JwtTokenProvider generates tokens:
   - Access token (24 hours)
   - Refresh token (7 days)

5. Returns tokens to client
```

### **Login Flow:**

```
1. Client sends POST /api/auth/login
   Body: { email, password }

2. AuthController receives request

3. AuthenticationManager:
   - Calls UserDetailsServiceImpl
   - Loads user from database
   - Compares passwords (BCrypt)

4. If valid:
   - JwtTokenProvider generates tokens
   - Returns tokens to client

5. If invalid:
   - Throws BadCredentialsException
   - Returns 401 Unauthorized
```

### **Protected Request Flow:**

```
1. Client sends request with token
   Header: Authorization: Bearer <token>

2. JwtAuthenticationFilter intercepts:
   - Extracts token from header
   - Validates token signature
   - Checks expiration
   - Extracts email from token

3. If valid:
   - Loads user from database
   - Sets authentication in SecurityContext
   - Request proceeds to controller

4. If invalid:
   - Request proceeds without authentication
   - Controller returns 401 if endpoint is protected
```

---

## 🎯 Key Security Concepts

### **1. JWT (JSON Web Token)**
```
Header.Payload.Signature

Header: { "alg": "HS512", "typ": "JWT" }
Payload: { "sub": "user@example.com", "userId": 1, "exp": 1630543189 }
Signature: HMACSHA512(base64(header) + "." + base64(payload), secret)
```

### **2. BCrypt Password Hashing**
```
Plain password: "password123"
Hashed: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"

- $2a$ = BCrypt algorithm
- 10 = Cost factor (2^10 rounds)
- Random salt included
- Same password = Different hash each time
```

### **3. Stateless Authentication**
- No sessions stored on server
- Each request includes JWT token
- Server validates token
- Scalable (no session storage)

---

## 🔒 Security Best Practices Implemented

1. ✅ **Password Hashing** - BCrypt with salt
2. ✅ **JWT Tokens** - Signed and time-limited
3. ✅ **Stateless** - No server-side sessions
4. ✅ **HTTPS Required** - In production
5. ✅ **Token Expiration** - 24 hours for access token
6. ✅ **Refresh Tokens** - 7 days for refresh
7. ✅ **Role-Based Access** - USER, ADMIN roles
8. ✅ **CORS Configuration** - Controlled origins
9. ✅ **SQL Injection Prevention** - JPA parameterized queries
10. ✅ **Error Handling** - No sensitive info in errors

---

**Next:** Read `04-ENTITIES-EXPLAINED.md` to understand database tables! 🗄️
