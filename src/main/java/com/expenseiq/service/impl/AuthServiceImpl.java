package com.expenseiq.service.impl;

import com.expenseiq.dto.request.LoginRequest;
import com.expenseiq.dto.request.RegisterRequest;
import com.expenseiq.dto.response.AuthResponse;
import com.expenseiq.dto.response.UserResponse;
import com.expenseiq.entity.User;
import com.expenseiq.enums.Role;
import com.expenseiq.exception.DuplicateResourceException;
import com.expenseiq.repository.UserRepository;
import com.expenseiq.security.JwtTokenProvider;
import com.expenseiq.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Create new user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .currency(request.getCurrency())
                .role(Role.USER)
                .enabled(true)
                .build();

        user = userRepository.save(user);

        // Generate tokens
        String token = tokenProvider.generateTokenFromEmail(user.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());

        // Build response
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profilePicture(user.getProfilePicture())
                .currency(user.getCurrency())
                .build();

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .expiresIn(jwtExpirationMs)
                .user(userResponse)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate tokens
        String token = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(request.getEmail());

        // Get user details
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profilePicture(user.getProfilePicture())
                .currency(user.getCurrency())
                .build();

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .expiresIn(jwtExpirationMs)
                .user(userResponse)
                .build();
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (tokenProvider.validateToken(refreshToken)) {
            String email = tokenProvider.getEmailFromToken(refreshToken);
            String newToken = tokenProvider.generateTokenFromEmail(email);
            String newRefreshToken = tokenProvider.generateRefreshToken(email);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserResponse userResponse = UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .profilePicture(user.getProfilePicture())
                    .currency(user.getCurrency())
                    .build();

            return AuthResponse.builder()
                    .token(newToken)
                    .refreshToken(newRefreshToken)
                    .type("Bearer")
                    .expiresIn(jwtExpirationMs)
                    .user(userResponse)
                    .build();
        }
        throw new RuntimeException("Invalid refresh token");
    }
}
