package com.expensetracker.app.dto;

/**
 * DTO for authentication responses containing JWT token and user info.
 *
 * @author Expense Tracker Team
 * @version 1.0
 */
public class AuthResponse {

    private String token;
    private String username;
    private String email;
    private String message;

    // Default constructor
    public AuthResponse() {
    }

    // Constructor with parameters
    public AuthResponse(String token, String username, String email, String message) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.message = message;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}