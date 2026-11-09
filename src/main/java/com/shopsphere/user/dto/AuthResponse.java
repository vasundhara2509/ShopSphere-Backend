package com.shopsphere.user.dto;

public class AuthResponse {

    private String message;
    private Long userId;
    private String fullName;
    private String email;
    private String token;

    public AuthResponse() {
    }

    public AuthResponse(String message, Long userId, String fullName,
                        String email, String token) {
        this.message = message;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }
}