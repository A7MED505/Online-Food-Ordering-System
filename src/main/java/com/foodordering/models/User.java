package com.foodordering.models;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * User entity representing authentication data.
 * Passwords are stored as SHA-256 hashes (demo purpose).
 */
public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;

    public User(int id, String username, String email, String rawPassword) {
        validateInput(username, email, rawPassword);
        this.id = id;
        this.username = username;
        this.email = email;
        setPassword(rawPassword);
    }

    private void validateInput(String username, String email, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email must be valid (e.g., user@example.com)");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Set password by hashing the raw value using SHA-256.
     * @param rawPassword plain text password
     */
    public void setPassword(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        this.passwordHash = hashPassword(rawPassword);
    }

    /**
     * Verify raw password against stored hash.
     * @param rawPassword plain password to check
     * @return true if matches
     */
    public boolean verifyPassword(String rawPassword) {
        if (rawPassword == null) {
            return false;
        }
        String hashed = hashPassword(rawPassword);
        return hashed.equals(this.passwordHash);
    }

    private String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
