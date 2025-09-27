package com.expensetracker.app.service;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.expensetracker.app.dto.AuthResponse;
import com.expensetracker.app.dto.LoginRequest;
import com.expensetracker.app.dto.RegisterRequest;
import com.expensetracker.app.model.User;
import com.expensetracker.app.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * Service for handling authentication operations.
 *
 * @author Expense Tracker Team
 * @version 1.0
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // Use a fixed secret key for consistency across restarts
    private final String jwtSecret = "expenseTrackerSecretKey2024!@#$%^&*()";
    private final Key jwtSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    private final long jwtExpirationMs = 86400000; // 24 hours

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user.
     *
     * @param registerRequest the registration request
     * @return AuthResponse with JWT token
     * @throws RuntimeException if user already exists
     */
    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        userRepository.save(user);

        // Generate JWT token
        String token = generateJwtToken(user);

        return new AuthResponse(token, user.getUsername(), user.getEmail(), "User registered successfully");
    }

    /**
     * Authenticate a user.
     *
     * @param loginRequest the login request
     * @return AuthResponse with JWT token
     * @throws RuntimeException if authentication fails
     */
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate JWT token
        String token = generateJwtToken(user);

        return new AuthResponse(token, user.getUsername(), user.getEmail(), "Login successful");
    }

    /**
     * Generate JWT token for user.
     *
     * @param user the user
     * @return JWT token string
     */
    private String generateJwtToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey)
                .compact();
    }

    /**
     * Validate JWT token and extract username.
     *
     * @param token the JWT token
     * @return username if valid, null otherwise
     */
    public String validateTokenAndGetUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get user by username.
     *
     * @param username the username
     * @return the user
     * @throws RuntimeException if user not found
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Update user.
     *
     * @param user the user to update
     * @return the updated user
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}