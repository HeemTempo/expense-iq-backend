# 📱 M-Pesa Direct API Integration Guide

## 🎯 Overview

Complete guide for integrating Safaricom M-Pesa Daraja API directly into ExpenseIQ backend.

---

## 📋 Prerequisites

### **1. Business Requirements:**
- Registered business in Kenya
- Safaricom M-Pesa Paybill or Till Number
- Business bank account

### **2. Developer Account:**
- Register at: https://developer.safaricom.co.ke
- Create app to get credentials
- Access to sandbox for testing

### **3. Credentials Needed:**
- Consumer Key
- Consumer Secret
- Shortcode (Business Number)
- Passkey
- Initiator Name
- Security Credential

---

## 🔧 Implementation Steps

### **Step 1: Add Dependencies**

**pom.xml:**
```xml
<!-- For HTTP requests -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>

<!-- For Base64 encoding -->
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
</dependency>
```

---

### **Step 2: Configuration**

**application.yml:**
```yaml
mpesa:
  # Sandbox URLs (for testing)
  sandbox:
    base-url: https://sandbox.safaricom.co.ke
    oauth-url: https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials
    stk-push-url: https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest
    callback-url: ${APP_URL}/api/mpesa/callback
  
  # Production URLs
  production:
    base-url: https://api.safaricom.co.ke
    oauth-url: https://api.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials
    stk-push-url: https://api.safaricom.co.ke/mpesa/stkpush/v1/processrequest
    callback-url: ${APP_URL}/api/mpesa/callback
  
  # Credentials (use environment variables in production!)
  consumer-key: ${MPESA_CONSUMER_KEY}
  consumer-secret: ${MPESA_CONSUMER_SECRET}
  shortcode: ${MPESA_SHORTCODE:174379}
  passkey: ${MPESA_PASSKEY}
  initiator-name: ${MPESA_INITIATOR_NAME}
  security-credential: ${MPESA_SECURITY_CREDENTIAL}
  
  # Environment: sandbox or production
  environment: sandbox
```

---

### **Step 3: Create Configuration Class**

**MpesaConfig.java:**
```java
package com.expenseiq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConfigurationProperties(prefix = "mpesa")
@Data
public class MpesaConfig {
    
    private Sandbox sandbox;
    private Production production;
    private String consumerKey;
    private String consumerSecret;
    private String shortcode;
    private String passkey;
    private String initiatorName;
    private String securityCredential;
    private String environment;
    
    @Data
    public static class Sandbox {
        private String baseUrl;
        private String oauthUrl;
        private String stkPushUrl;
        private String callbackUrl;
    }
    
    @Data
    public static class Production {
        private String baseUrl;
        private String oauthUrl;
        private String stkPushUrl;
        private String callbackUrl;
    }
    
    @Bean
    public WebClient mpesaWebClient() {
        String baseUrl = "sandbox".equals(environment) 
            ? sandbox.getBaseUrl() 
            : production.getBaseUrl();
            
        return WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }
    
    public String getOauthUrl() {
        return "sandbox".equals(environment) 
            ? sandbox.getOauthUrl() 
            : production.getOauthUrl();
    }
    
    public String getStkPushUrl() {
        return "sandbox".equals(environment) 
            ? sandbox.getStkPushUrl() 
            : production.getStkPushUrl();
    }
    
    public String getCallbackUrl() {
        return "sandbox".equals(environment) 
            ? sandbox.getCallbackUrl() 
            : production.getCallbackUrl();
    }
}
```

---

### **Step 4: Create DTOs**

**MpesaStkPushRequest.java:**
```java
package com.expenseiq.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MpesaStkPushRequest {
    private String phoneNumber;
    private Double amount;
    private String accountReference;
    private String transactionDesc;
}
```

**MpesaStkPushResponse.java:**
```java
package com.expenseiq.dto.response;

import lombok.Data;

@Data
public class MpesaStkPushResponse {
    private String MerchantRequestID;
    private String CheckoutRequestID;
    private String ResponseCode;
    private String ResponseDescription;
    private String CustomerMessage;
}
```

**MpesaCallbackRequest.java:**
```java
package com.expenseiq.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class MpesaCallbackRequest {
    private Body Body;
    
    @Data
    public static class Body {
        private StkCallback stkCallback;
    }
    
    @Data
    public static class StkCallback {
        private String MerchantRequestID;
        private String CheckoutRequestID;
        private Integer ResultCode;
        private String ResultDesc;
        private CallbackMetadata CallbackMetadata;
    }
    
    @Data
    public static class CallbackMetadata {
        private List<Item> Item;
    }
    
    @Data
    public static class Item {
        private String Name;
        private Object Value;
    }
}
```

---

### **Step 5: Create M-Pesa Service**

**MpesaService.java:**
```java
package com.expenseiq.service.impl;

import com.expenseiq.config.MpesaConfig;
import com.expenseiq.dto.request.MpesaStkPushRequest;
import com.expenseiq.dto.response.MpesaStkPushResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpesaService {
    
    private final MpesaConfig mpesaConfig;
    private final WebClient mpesaWebClient;
    
    /**
     * Get OAuth access token from M-Pesa
     */
    public String getAccessToken() {
        String auth = mpesaConfig.getConsumerKey() + ":" + mpesaConfig.getConsumerSecret();
        String encodedAuth = Base64.encodeBase64String(auth.getBytes(StandardCharsets.UTF_8));
        
        Map<String, Object> response = mpesaWebClient.get()
            .uri(mpesaConfig.getOauthUrl())
            .header("Authorization", "Basic " + encodedAuth)
            .retrieve()
            .bodyToMono(Map.class)
            .block();
        
        return (String) response.get("access_token");
    }
    
    /**
     * Generate password for STK Push
     */
    private String generatePassword() {
        String timestamp = getTimestamp();
        String data = mpesaConfig.getShortcode() + mpesaConfig.getPasskey() + timestamp;
        return Base64.encodeBase64String(data.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Get current timestamp in M-Pesa format
     */
    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }
    
    /**
     * Format phone number to M-Pesa format (254XXXXXXXXX)
     */
    private String formatPhoneNumber(String phone) {
        // Remove spaces, dashes, plus
        phone = phone.replaceAll("[\\s\\-\\+]", "");
        
        // If starts with 0, replace with 254
        if (phone.startsWith("0")) {
            phone = "254" + phone.substring(1);
        }
        
        // If doesn't start with 254, add it
        if (!phone.startsWith("254")) {
            phone = "254" + phone;
        }
        
        return phone;
    }
    
    /**
     * Initiate STK Push (Lipa Na M-Pesa Online)
     */
    public MpesaStkPushResponse stkPush(MpesaStkPushRequest request) {
        String accessToken = getAccessToken();
        String timestamp = getTimestamp();
        String password = generatePassword();
        String phoneNumber = formatPhoneNumber(request.getPhoneNumber());
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("BusinessShortCode", mpesaConfig.getShortcode());
        payload.put("Password", password);
        payload.put("Timestamp", timestamp);
        payload.put("TransactionType", "CustomerPayBillOnline");
        payload.put("Amount", request.getAmount().intValue());
        payload.put("PartyA", phoneNumber);
        payload.put("PartyB", mpesaConfig.getShortcode());
        payload.put("PhoneNumber", phoneNumber);
        payload.put("CallBackURL", mpesaConfig.getCallbackUrl());
        payload.put("AccountReference", request.getAccountReference());
        payload.put("TransactionDesc", request.getTransactionDesc());
        
        log.info("Initiating STK Push for phone: {}, amount: {}", phoneNumber, request.getAmount());
        
        MpesaStkPushResponse response = mpesaWebClient.post()
            .uri(mpesaConfig.getStkPushUrl())
            .header("Authorization", "Bearer " + accessToken)
            .header("Content-Type", "application/json")
            .bodyValue(payload)
            .retrieve()
            .bodyToMono(MpesaStkPushResponse.class)
            .block();
        
        log.info("STK Push response: {}", response);
        
        return response;
    }
    
    /**
     * Query STK Push transaction status
     */
    public Map<String, Object> queryStkPushStatus(String checkoutRequestId) {
        String accessToken = getAccessToken();
        String timestamp = getTimestamp();
        String password = generatePassword();
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("BusinessShortCode", mpesaConfig.getShortcode());
        payload.put("Password", password);
        payload.put("Timestamp", timestamp);
        payload.put("CheckoutRequestID", checkoutRequestId);
        
        return mpesaWebClient.post()
            .uri("/mpesa/stkpushquery/v1/query")
            .header("Authorization", "Bearer " + accessToken)
            .header("Content-Type", "application/json")
            .bodyValue(payload)
            .retrieve()
            .bodyToMono(Map.class)
            .block();
    }
}
```

---

### **Step 6: Create M-Pesa Payment Entity**

**MpesaPayment.java:**
```java
package com.expenseiq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mpesa_payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MpesaPayment extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "merchant_request_id")
    private String merchantRequestId;
    
    @Column(name = "checkout_request_id", unique = true)
    private String checkoutRequestId;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "account_reference")
    private String accountReference;
    
    @Column(name = "transaction_desc")
    private String transactionDesc;
    
    @Column(name = "mpesa_receipt_number")
    private String mpesaReceiptNumber;
    
    @Column(name = "result_code")
    private Integer resultCode;
    
    @Column(name = "result_desc")
    private String resultDesc;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MpesaPaymentStatus status;
    
    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
```

**MpesaPaymentStatus.java:**
```java
package com.expenseiq.enums;

public enum MpesaPaymentStatus {
    PENDING,      // STK Push sent
    PROCESSING,   // User entered PIN
    SUCCESS,      // Payment successful
    FAILED,       // Payment failed
    CANCELLED,    // User cancelled
    TIMEOUT       // User didn't respond
}
```

---

### **Step 7: Create Repository**

**MpesaPaymentRepository.java:**
```java
package com.expenseiq.repository;

import com.expenseiq.entity.MpesaPayment;
import com.expenseiq.enums.MpesaPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MpesaPaymentRepository extends JpaRepository<MpesaPayment, Long> {
    
    Optional<MpesaPayment> findByCheckoutRequestId(String checkoutRequestId);
    
    Optional<MpesaPayment> findByMerchantRequestId(String merchantRequestId);
    
    List<MpesaPayment> findByUserId(Long userId);
    
    List<MpesaPayment> findByStatus(MpesaPaymentStatus status);
    
    List<MpesaPayment> findByUserIdOrderByCreatedAtDesc(Long userId);
}
```

---

### **Step 8: Create Controller**

**MpesaController.java:**
```java
package com.expenseiq.controller;

import com.expenseiq.dto.request.MpesaCallbackRequest;
import com.expenseiq.dto.request.MpesaStkPushRequest;
import com.expenseiq.dto.response.ApiResponse;
import com.expenseiq.dto.response.MpesaStkPushResponse;
import com.expenseiq.entity.MpesaPayment;
import com.expenseiq.security.SecurityUser;
import com.expenseiq.service.MpesaPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/mpesa")
@RequiredArgsConstructor
@Slf4j
public class MpesaController {
    
    private final MpesaPaymentService mpesaPaymentService;
    
    @PostMapping("/stk-push")
    public ResponseEntity<ApiResponse<MpesaStkPushResponse>> initiateStkPush(
            @Valid @RequestBody MpesaStkPushRequest request,
            @AuthenticationPrincipal SecurityUser currentUser) {
        
        log.info("STK Push request from user: {}", currentUser.getId());
        
        MpesaStkPushResponse response = mpesaPaymentService.initiateStkPush(
            request, 
            currentUser.getId()
        );
        
        return ResponseEntity.ok(ApiResponse.success(
            "STK Push sent. Please check your phone to complete payment",
            response
        ));
    }
    
    @PostMapping("/callback")
    public ResponseEntity<Void> handleCallback(@RequestBody MpesaCallbackRequest callback) {
        log.info("M-Pesa callback received: {}", callback);
        
        mpesaPaymentService.processCallback(callback);
        
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<List<MpesaPayment>>> getUserPayments(
            @AuthenticationPrincipal SecurityUser currentUser) {
        
        List<MpesaPayment> payments = mpesaPaymentService.getUserPayments(currentUser.getId());
        
        return ResponseEntity.ok(ApiResponse.success(
            "Payments retrieved successfully",
            payments
        ));
    }
    
    @GetMapping("/payments/{checkoutRequestId}/status")
    public ResponseEntity<ApiResponse<MpesaPayment>> checkPaymentStatus(
            @PathVariable String checkoutRequestId) {
        
        MpesaPayment payment = mpesaPaymentService.checkPaymentStatus(checkoutRequestId);
        
        return ResponseEntity.ok(ApiResponse.success(
            "Payment status retrieved",
            payment
        ));
    }
}
```

---

### **Step 9: Create Payment Service**

**MpesaPaymentService.java:**
```java
package com.expenseiq.service.impl;

import com.expenseiq.dto.request.MpesaCallbackRequest;
import com.expenseiq.dto.request.MpesaStkPushRequest;
import com.expenseiq.dto.response.MpesaStkPushResponse;
import com.expenseiq.entity.MpesaPayment;
import com.expenseiq.entity.Transaction;
import com.expenseiq.entity.User;
import com.expenseiq.enums.MpesaPaymentStatus;
import com.expenseiq.enums.TransactionType;
import com.expenseiq.exception.ResourceNotFoundException;
import com.expenseiq.repository.MpesaPaymentRepository;
import com.expenseiq.repository.TransactionRepository;
import com.expenseiq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpesaPaymentService {
    
    private final MpesaService mpesaService;
    private final MpesaPaymentRepository mpesaPaymentRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    
    @Transactional
    public MpesaStkPushResponse initiateStkPush(MpesaStkPushRequest request, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Call M-Pesa API
        MpesaStkPushResponse response = mpesaService.stkPush(request);
        
        // Save payment record
        MpesaPayment payment = MpesaPayment.builder()
            .user(user)
            .merchantRequestId(response.getMerchantRequestID())
            .checkoutRequestId(response.getCheckoutRequestID())
            .phoneNumber(request.getPhoneNumber())
            .amount(BigDecimal.valueOf(request.getAmount()))
            .accountReference(request.getAccountReference())
            .transactionDesc(request.getTransactionDesc())
            .status(MpesaPaymentStatus.PENDING)
            .build();
        
        mpesaPaymentRepository.save(payment);
        
        return response;
    }
    
    @Transactional
    public void processCallback(MpesaCallbackRequest callback) {
        String checkoutRequestId = callback.getBody().getStkCallback().getCheckoutRequestID();
        Integer resultCode = callback.getBody().getStkCallback().getResultCode();
        String resultDesc = callback.getBody().getStkCallback().getResultDesc();
        
        MpesaPayment payment = mpesaPaymentRepository.findByCheckoutRequestId(checkoutRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        payment.setResultCode(resultCode);
        payment.setResultDesc(resultDesc);
        
        if (resultCode == 0) {
            // Payment successful
            payment.setStatus(MpesaPaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            
            // Extract M-Pesa receipt number
            var metadata = callback.getBody().getStkCallback().getCallbackMetadata();
            if (metadata != null && metadata.getItem() != null) {
                for (var item : metadata.getItem()) {
                    if ("MpesaReceiptNumber".equals(item.getName())) {
                        payment.setMpesaReceiptNumber(item.getValue().toString());
                    }
                }
            }
            
            // Create transaction automatically
            createTransactionFromPayment(payment);
            
        } else {
            // Payment failed
            payment.setStatus(MpesaPaymentStatus.FAILED);
        }
        
        mpesaPaymentRepository.save(payment);
    }
    
    private void createTransactionFromPayment(MpesaPayment payment) {
        // Auto-create transaction for successful payment
        // You can customize this based on your needs
        
        Transaction transaction = new Transaction();
        transaction.setUser(payment.getUser());
        transaction.setType(TransactionType.EXPENSE); // or INCOME based on context
        transaction.setAmount(payment.getAmount());
        transaction.setDescription("M-Pesa Payment: " + payment.getTransactionDesc());
        transaction.setDate(payment.getPaidAt().toLocalDate());
        // Set category and account as needed
        
        transaction = transactionRepository.save(transaction);
        
        payment.setTransaction(transaction);
        mpesaPaymentRepository.save(payment);
    }
    
    public List<MpesaPayment> getUserPayments(Long userId) {
        return mpesaPaymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public MpesaPayment checkPaymentStatus(String checkoutRequestId) {
        return mpesaPaymentRepository.findByCheckoutRequestId(checkoutRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }
}
```

---

## 🧪 Testing

### **1. Sandbox Test Credentials:**
```
Shortcode: 174379
Passkey: bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919
Test Phone: 254708374149
```

### **2. Test STK Push:**
```bash
POST http://localhost:8081/api/mpesa/stk-push
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "phoneNumber": "254708374149",
  "amount": 10.0,
  "accountReference": "TEST001",
  "transactionDesc": "Test Payment"
}
```

### **3. Simulate Callback (Sandbox):**
M-Pesa will call your callback URL automatically.

---

## 🚀 Go Live Checklist

- [ ] Register business with Safaricom
- [ ] Get production credentials
- [ ] Update `application.yml` environment to `production`
- [ ] Set environment variables for credentials
- [ ] Configure production callback URL
- [ ] Test with real phone numbers
- [ ] Monitor transactions
- [ ] Set up error alerts

---

**This is a complete M-Pesa integration ready to implement!**

**Last Updated:** November 7, 2025
