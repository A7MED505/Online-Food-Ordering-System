package com.foodordering.utils;

import com.foodordering.exceptions.ValidationException;

/**
 * Utility class for input validation across the application.
 * Provides common validation methods with meaningful error messages.
 */
public class ValidationUtils {

    /**
     * Validates that a string is not null or empty.
     * @param value The string to validate
     * @param fieldName Name of the field for error messages
     * @throws ValidationException if validation fails
     */
    public static void validateNotEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
    }

    /**
     * Validates email format.
     * @param email Email address to validate
     * @throws ValidationException if email format is invalid
     */
    public static void validateEmail(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }
        
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            throw new ValidationException("Invalid email format. Example: user@example.com");
        }
    }

    /**
     * Validates password meets minimum requirements.
     * @param password Password to validate
     * @throws ValidationException if password doesn't meet requirements
     */
    public static void validatePassword(String password) throws ValidationException {
        if (password == null) {
            throw new ValidationException("Password cannot be null");
        }
        if (password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long");
        }
        if (password.length() > 100) {
            throw new ValidationException("Password cannot exceed 100 characters");
        }
    }

    /**
     * Validates username meets requirements.
     * @param username Username to validate
     * @throws ValidationException if username doesn't meet requirements
     */
    public static void validateUsername(String username) throws ValidationException {
        validateNotEmpty(username, "Username");
        
        if (username.length() < 3) {
            throw new ValidationException("Username must be at least 3 characters long");
        }
        if (username.length() > 50) {
            throw new ValidationException("Username cannot exceed 50 characters");
        }
        
        String usernameRegex = "^[a-zA-Z0-9_]+$";
        if (!username.matches(usernameRegex)) {
            throw new ValidationException("Username can only contain letters, numbers, and underscores");
        }
    }

    /**
     * Validates phone number format.
     * @param phone Phone number to validate
     * @throws ValidationException if phone format is invalid
     */
    public static void validatePhone(String phone) throws ValidationException {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Phone number cannot be empty");
        }
        
        // Remove common formatting characters
        String cleanPhone = phone.replaceAll("[\\s()-]", "");
        
        if (cleanPhone.length() < 10 || cleanPhone.length() > 15) {
            throw new ValidationException("Phone number must be between 10 and 15 digits");
        }
        
        if (!cleanPhone.matches("^[0-9+]+$")) {
            throw new ValidationException("Phone number can only contain digits and + symbol");
        }
    }

    /**
     * Validates price is positive.
     * @param price Price to validate
     * @param fieldName Name of the field for error messages
     * @throws ValidationException if price is invalid
     */
    public static void validatePositivePrice(double price, String fieldName) throws ValidationException {
        if (price < 0) {
            throw new ValidationException(fieldName + " cannot be negative");
        }
        if (price > 999999.99) {
            throw new ValidationException(fieldName + " exceeds maximum allowed value");
        }
    }

    /**
     * Validates quantity is positive.
     * @param quantity Quantity to validate
     * @param fieldName Name of the field for error messages
     * @throws ValidationException if quantity is invalid
     */
    public static void validatePositiveQuantity(int quantity, String fieldName) throws ValidationException {
        if (quantity <= 0) {
            throw new ValidationException(fieldName + " must be greater than zero");
        }
        if (quantity > 999) {
            throw new ValidationException(fieldName + " exceeds maximum allowed value (999)");
        }
    }

    /**
     * Validates rating is within valid range (0-5).
     * @param rating Rating to validate
     * @throws ValidationException if rating is invalid
     */
    public static void validateRating(int rating) throws ValidationException {
        if (rating < 0 || rating > 5) {
            throw new ValidationException("Rating must be between 0 and 5");
        }
    }

    /**
     * Validates ID is positive.
     * @param id ID to validate
     * @param fieldName Name of the field for error messages
     * @throws ValidationException if ID is invalid
     */
    public static void validatePositiveId(int id, String fieldName) throws ValidationException {
        if (id < 0) {
            throw new ValidationException(fieldName + " ID cannot be negative");
        }
    }

    /**
     * Validates string length is within range.
     * @param value String to validate
     * @param fieldName Name of the field for error messages
     * @param minLength Minimum allowed length
     * @param maxLength Maximum allowed length
     * @throws ValidationException if length is invalid
     */
    public static void validateLength(String value, String fieldName, int minLength, int maxLength) 
            throws ValidationException {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
        
        int length = value.trim().length();
        if (length < minLength) {
            throw new ValidationException(
                fieldName + " must be at least " + minLength + " characters long");
        }
        if (length > maxLength) {
            throw new ValidationException(
                fieldName + " cannot exceed " + maxLength + " characters");
        }
    }
}
