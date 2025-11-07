package com.expenseiq.service;

import com.expenseiq.dto.request.LoginRequest;
import com.expenseiq.dto.request.RegisterRequest;
import com.expenseiq.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
}
