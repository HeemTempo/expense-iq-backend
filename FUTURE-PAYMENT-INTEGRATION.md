# 💳 Future Payment Integration Guide

## 📋 Overview

This document outlines how to add payment integration to ExpenseIQ backend in the future.

---

## 🎯 Three Integration Options

### **Option 1: SMS Parsing (Recommended First)**
- Auto-import transactions from M-Pesa/Airtel SMS
- Free, no API costs
- Requires mobile app

### **Option 2: Flutterwave Integration**
- Accept payments via M-Pesa, Airtel, Cards, Banks
- Transaction fees: 1.4% - 3.8%
- Professional payment solution

### **Option 3: Direct M-Pesa API**
- Direct integration with Safaricom
- M-Pesa only
- Requires business registration

---

## 📱 Option 1: SMS Parsing Implementation

### **What You Need:**
1. Mobile app (Android/iOS)
2. SMS read permission
3. SMS parser service

### **Backend Changes Required:**

#### **1. Add New Entity: `SmsTransaction.java`**
```java
@Entity
@Table(name = "sms_transactions")
public class SmsTransaction extends BaseEntity {
    private String smsBody;
    private String sender;
    private LocalDateTime receivedAt;
    private String transactionCode;
    private BigDecimal amount;
    private String merchantName;
    private TransactionType type;
    private Boolean processed;
    private Long transactionId; // Link to created transaction
}
```

#### **2. Add Repository: `SmsTransactionRepository.java`**
```java
public interface SmsTransactionRepository extends JpaRepository<SmsTransaction, Long> {
    List<SmsTransaction> findByProcessedFalse();
    Optional<SmsTransaction> findByTransactionCode(String code);
}
```

#### **3. Add Service: `SmsParserService.java`**
```java
@Service
public class SmsParserService {
    
    public ParsedSms parseMpesaSms(String smsBody) {
        // Parse: "Confirmed. Ksh500.00 paid to SHOP X..."
        // Extract: amount, merchant, code, type
    }
    
    public ParsedSms parseAirtelSms(String smsBody) {
        // Parse Airtel Money SMS format
    }
    
    public Transaction createTransactionFromSms(ParsedSms parsed, User user) {
        // Auto-create transaction
        // Suggest category based on merchant
    }
}
```

#### **4. Add Endpoint: `SmsController.java`**
```java
@RestController
@RequestMapping("/api/sms")
public class SmsController {
    
    @PostMapping("/import")
    public ApiResponse<Transaction> importSms(@RequestBody SmsImportRequest request) {
        // Mobile app sends SMS content
        // Backend parses and creates transaction
    }
    
    @GetMapping("/pending")
    public ApiResponse<List<SmsTransaction>> getPendingSms() {
        // Get unparsed SMS for user review
    }
}
```

### **Mobile App Changes:**
- Request SMS permission
- Read M-Pesa/Airtel SMS
- Send to backend API
- User confirms/edits before saving

---

## 💰 Option 2: Flutterwave Integration

### **What You Need:**
1. Flutterwave account (https://flutterwave.com)
2. API keys (Public & Secret)
3. Business registration

### **Backend Changes Required:**

#### **1. Add Dependency to `pom.xml`:**
```xml
<dependency>
    <groupId>com.flutterwave</groupId>
    <artifactId>flutterwave-java</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Or use HTTP client -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

#### **2. Add Configuration to `application.yml`:**
```yaml
flutterwave:
  public-key: ${FLUTTERWAVE_PUBLIC_KEY}
  secret-key: ${FLUTTERWAVE_SECRET_KEY}
  encryption-key: ${FLUTTERWAVE_ENCRYPTION_KEY}
  base-url: https://api.flutterwave.com/v3
  webhook-secret: ${FLUTTERWAVE_WEBHOOK_SECRET}
```

#### **3. Create Config: `FlutterwaveConfig.java`**
```java
@Configuration
public class FlutterwaveConfig {
    
    @Value("${flutterwave.secret-key}")
    private String secretKey;
    
    @Value("${flutterwave.public-key}")
    private String publicKey;
    
    @Bean
    public WebClient flutterwaveClient() {
        return WebClient.builder()
            .baseUrl("https://api.flutterwave.com/v3")
            .defaultHeader("Authorization", "Bearer " + secretKey)
            .build();
    }
}
```

#### **4. Create Entity: `Payment.java`**
```java
@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {
    
    @ManyToOne
    private User user;
    
    private String flutterwaveTransactionId;
    private String flutterwaveReference;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // PENDING, SUCCESS, FAILED
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod method; // MPESA, AIRTEL, CARD, BANK
    
    private BigDecimal amount;
    private String currency;
    private String customerEmail;
    private String customerPhone;
    
    @ManyToOne
    private Transaction transaction; // Linked transaction
    
    private String failureReason;
    private LocalDateTime paidAt;
}
```

#### **5. Create Enum: `PaymentStatus.java`**
```java
public enum PaymentStatus {
    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    CANCELLED,
    REFUNDED
}
```

#### **6. Create Enum: `PaymentMethod.java`**
```java
public enum PaymentMethod {
    MPESA,
    AIRTEL_MONEY,
    MTN_MONEY,
    TIGO_PESA,
    CARD,
    BANK_TRANSFER,
    WALLET
}
```

#### **7. Create DTOs:**

**PaymentRequest.java:**
```java
@Data
public class PaymentRequest {
    @NotNull
    private BigDecimal amount;
    
    @NotBlank
    private String currency; // KES, UGX, TZS
    
    @NotNull
    private PaymentMethod paymentMethod;
    
    @Email
    private String email;
    
    @NotBlank
    private String phoneNumber; // For mobile money
    
    private String description;
    
    // Optional: Link to transaction
    private Long transactionId;
}
```

**PaymentResponse.java:**
```java
@Data
@Builder
public class PaymentResponse {
    private Long id;
    private String flutterwaveReference;
    private String paymentLink; // For card payments
    private PaymentStatus status;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod method;
    private LocalDateTime createdAt;
}
```

#### **8. Create Service: `FlutterwaveService.java`**
```java
@Service
@RequiredArgsConstructor
public class FlutterwaveService {
    
    private final WebClient flutterwaveClient;
    private final PaymentRepository paymentRepository;
    
    public PaymentResponse initiateMpesaPayment(PaymentRequest request, User user) {
        // 1. Create payment record
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(request.getAmount());
        payment.setMethod(PaymentMethod.MPESA);
        payment.setStatus(PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);
        
        // 2. Call Flutterwave API
        Map<String, Object> payload = Map.of(
            "tx_ref", "TXN-" + payment.getId(),
            "amount", request.getAmount(),
            "currency", request.getCurrency(),
            "payment_type", "mobilemoneykenya",
            "customer", Map.of(
                "email", request.getEmail(),
                "phonenumber", request.getPhoneNumber(),
                "name", user.getName()
            ),
            "customizations", Map.of(
                "title", "ExpenseIQ Payment",
                "description", request.getDescription()
            )
        );
        
        // 3. Make API call
        FlutterwaveResponse response = flutterwaveClient.post()
            .uri("/charges?type=mobile_money_kenya")
            .bodyValue(payload)
            .retrieve()
            .bodyToMono(FlutterwaveResponse.class)
            .block();
        
        // 4. Update payment
        payment.setFlutterwaveTransactionId(response.getData().getId());
        payment.setFlutterwaveReference(response.getData().getFlwRef());
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);
        
        // 5. Return response
        return PaymentResponse.builder()
            .id(payment.getId())
            .flutterwaveReference(payment.getFlutterwaveReference())
            .status(payment.getStatus())
            .amount(payment.getAmount())
            .currency(request.getCurrency())
            .method(PaymentMethod.MPESA)
            .createdAt(payment.getCreatedAt())
            .build();
    }
    
    public PaymentResponse verifyPayment(String transactionId) {
        // Call Flutterwave verify endpoint
        // Update payment status
        // Create transaction if successful
    }
}
```

#### **9. Create Controller: `PaymentController.java`**
```java
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final FlutterwaveService flutterwaveService;
    
    @PostMapping("/mpesa")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiateMpesa(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal SecurityUser currentUser) {
        
        PaymentResponse response = flutterwaveService.initiateMpesaPayment(
            request, 
            currentUser.getUser()
        );
        
        return ResponseEntity.ok(ApiResponse.success(
            "Payment initiated. Check your phone for M-Pesa prompt",
            response
        ));
    }
    
    @GetMapping("/{id}/verify")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(
            @PathVariable Long id) {
        
        PaymentResponse response = flutterwaveService.verifyPayment(id);
        
        return ResponseEntity.ok(ApiResponse.success(
            "Payment verified",
            response
        ));
    }
    
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader("verif-hash") String verifHash) {
        
        // Verify webhook signature
        // Update payment status
        // Create transaction if successful
        
        return ResponseEntity.ok().build();
    }
}
```

#### **10. Create Repository: `PaymentRepository.java`**
```java
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    Optional<Payment> findByFlutterwaveTransactionId(String txId);
    List<Payment> findByStatus(PaymentStatus status);
}
```

---

## 📞 Option 3: Direct M-Pesa API (Safaricom)

### **What You Need:**
1. Safaricom Developer account
2. Business shortcode
3. Consumer Key & Secret
4. Passkey

### **Implementation Steps:**

See separate file: `MPESA-INTEGRATION-GUIDE.md`

---

## 🗂️ Database Schema Changes

### **New Tables Needed:**

```sql
-- For SMS Parsing
CREATE TABLE sms_transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    sms_body TEXT,
    sender VARCHAR(50),
    received_at TIMESTAMP,
    transaction_code VARCHAR(50),
    amount DECIMAL(15,2),
    merchant_name VARCHAR(255),
    type VARCHAR(20),
    processed BOOLEAN DEFAULT FALSE,
    transaction_id BIGINT REFERENCES transactions(id),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- For Payment Processing
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    flutterwave_transaction_id VARCHAR(255),
    flutterwave_reference VARCHAR(255),
    status VARCHAR(20),
    method VARCHAR(50),
    amount DECIMAL(15,2),
    currency VARCHAR(3),
    customer_email VARCHAR(255),
    customer_phone VARCHAR(20),
    transaction_id BIGINT REFERENCES transactions(id),
    failure_reason TEXT,
    paid_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

---

## 📝 API Endpoints to Add

### **SMS Parsing:**
```
POST   /api/sms/import          - Import SMS
GET    /api/sms/pending         - Get pending SMS
POST   /api/sms/{id}/confirm    - Confirm parsed transaction
DELETE /api/sms/{id}            - Reject SMS
```

### **Payment Processing:**
```
POST   /api/payments/mpesa      - Initiate M-Pesa payment
POST   /api/payments/airtel     - Initiate Airtel payment
POST   /api/payments/card       - Initiate card payment
GET    /api/payments/{id}       - Get payment details
GET    /api/payments/{id}/verify - Verify payment status
GET    /api/payments            - List user payments
POST   /api/payments/webhook    - Webhook for payment updates
```

---

## 🔐 Security Considerations

### **For SMS Parsing:**
- Validate SMS sender
- Sanitize SMS content
- Rate limit SMS imports
- User confirmation required

### **For Payment Processing:**
- Verify webhook signatures
- Use HTTPS only
- Store API keys in environment variables
- Implement idempotency
- Add fraud detection
- Log all payment attempts

---

## 💰 Cost Estimates

### **SMS Parsing:**
- **Cost:** FREE
- **Effort:** Medium
- **Time:** 1-2 weeks

### **Flutterwave:**
- **Setup:** FREE
- **Transaction Fee:** 1.4% - 3.8%
- **Effort:** High
- **Time:** 2-3 weeks

### **Direct M-Pesa:**
- **Setup:** FREE (requires business)
- **Transaction Fee:** Varies
- **Effort:** Very High
- **Time:** 3-4 weeks

---

## 📚 Resources

### **Flutterwave:**
- Docs: https://developer.flutterwave.com
- API Reference: https://developer.flutterwave.com/reference
- Test Mode: Available

### **M-Pesa (Safaricom):**
- Docs: https://developer.safaricom.co.ke
- Daraja API: https://developer.safaricom.co.ke/APIs
- Sandbox: Available

### **Paystack:**
- Docs: https://paystack.com/docs
- API: https://paystack.com/docs/api

---

## 🎯 Recommended Implementation Order

### **Phase 1: SMS Parsing** (Start Here)
1. Create entities and repositories
2. Build SMS parser service
3. Add API endpoints
4. Test with sample SMS
5. Build mobile app integration

### **Phase 2: Flutterwave** (If needed)
1. Register business account
2. Get API keys
3. Implement payment flow
4. Add webhook handling
5. Test in sandbox
6. Go live

### **Phase 3: Additional Providers**
1. Add Airtel Money
2. Add bank transfers
3. Add card payments
4. Add other mobile money

---

## ✅ Checklist Before Implementation

- [ ] Decide which option to implement
- [ ] Register business accounts (if needed)
- [ ] Get API credentials
- [ ] Set up test environment
- [ ] Review security requirements
- [ ] Plan database changes
- [ ] Design API endpoints
- [ ] Create documentation
- [ ] Test thoroughly
- [ ] Deploy to production

---

**This guide will be updated as implementation progresses.**

**Last Updated:** November 7, 2025  
**Status:** Planning Phase  
**Next Step:** Choose integration option
