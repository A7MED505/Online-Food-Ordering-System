package com.foodordering.utils;

import com.foodordering.exceptions.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ValidationUtils class.
 * Verifies all validation methods work correctly.
 */
@DisplayName("Validation Utils Tests")
public class ValidationUtilsTest {

    // ========== Email Validation Tests ==========

    @Test
    @DisplayName("Valid email should pass validation")
    void testValidEmail() {
        assertDoesNotThrow(() -> ValidationUtils.validateEmail("user@example.com"));
        assertDoesNotThrow(() -> ValidationUtils.validateEmail("test.user@domain.co.uk"));
        assertDoesNotThrow(() -> ValidationUtils.validateEmail("name+tag@company.org"));
    }

    @Test
    @DisplayName("Invalid email should throw ValidationException")
    void testInvalidEmail() {
        ValidationException ex1 = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateEmail("invalid-email"));
        assertTrue(ex1.getMessage().contains("Invalid email format"));

        ValidationException ex2 = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateEmail("@example.com"));
        assertTrue(ex2.getMessage().contains("Invalid email format"));

        ValidationException ex3 = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateEmail("user@"));
        assertTrue(ex3.getMessage().contains("Invalid email format"));
    }

    @Test
    @DisplayName("Null or empty email should throw ValidationException")
    void testNullOrEmptyEmail() {
        assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateEmail(null));
        assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateEmail(""));
        assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateEmail("   "));
    }

    // ========== Password Validation Tests ==========

    @Test
    @DisplayName("Valid password should pass validation")
    void testValidPassword() {
        assertDoesNotThrow(() -> ValidationUtils.validatePassword("password123"));
        assertDoesNotThrow(() -> ValidationUtils.validatePassword("verylongpassword"));
        assertDoesNotThrow(() -> ValidationUtils.validatePassword("P@ssw0rd!"));
    }

    @Test
    @DisplayName("Short password should throw ValidationException")
    void testShortPassword() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validatePassword("short"));
        assertTrue(ex.getMessage().contains("at least 8 characters"));
    }

    @Test
    @DisplayName("Null password should throw ValidationException")
    void testNullPassword() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validatePassword(null));
        assertTrue(ex.getMessage().contains("cannot be null"));
    }

    @Test
    @DisplayName("Too long password should throw ValidationException")
    void testTooLongPassword() {
        String veryLongPassword = "a".repeat(101);
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validatePassword(veryLongPassword));
        assertTrue(ex.getMessage().contains("cannot exceed 100 characters"));
    }

    // ========== Username Validation Tests ==========

    @Test
    @DisplayName("Valid username should pass validation")
    void testValidUsername() {
        assertDoesNotThrow(() -> ValidationUtils.validateUsername("john_doe"));
        assertDoesNotThrow(() -> ValidationUtils.validateUsername("user123"));
        assertDoesNotThrow(() -> ValidationUtils.validateUsername("Test_User_2024"));
    }

    @Test
    @DisplayName("Short username should throw ValidationException")
    void testShortUsername() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateUsername("ab"));
        assertTrue(ex.getMessage().contains("at least 3 characters"));
    }

    @Test
    @DisplayName("Long username should throw ValidationException")
    void testLongUsername() {
        String longUsername = "a".repeat(51);
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateUsername(longUsername));
        assertTrue(ex.getMessage().contains("cannot exceed 50 characters"));
    }

    @Test
    @DisplayName("Username with special characters should throw ValidationException")
    void testUsernameWithSpecialChars() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateUsername("user@name"));
        assertTrue(ex.getMessage().contains("letters, numbers, and underscores"));
    }

    @Test
    @DisplayName("Empty username should throw ValidationException")
    void testEmptyUsername() {
        assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateUsername(""));
        assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateUsername("   "));
    }

    // ========== Phone Validation Tests ==========

    @Test
    @DisplayName("Valid phone should pass validation")
    void testValidPhone() {
        assertDoesNotThrow(() -> ValidationUtils.validatePhone("1234567890"));
        assertDoesNotThrow(() -> ValidationUtils.validatePhone("+1-234-567-8900"));
        assertDoesNotThrow(() -> ValidationUtils.validatePhone("(123) 456-7890"));
    }

    @Test
    @DisplayName("Short phone should throw ValidationException")
    void testShortPhone() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validatePhone("12345"));
        assertTrue(ex.getMessage().contains("between 10 and 15 digits"));
    }

    @Test
    @DisplayName("Phone with invalid characters should throw ValidationException")
    void testPhoneWithInvalidChars() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validatePhone("123-abc-7890"));
        assertTrue(ex.getMessage().contains("digits and + symbol"));
    }

    // ========== Price Validation Tests ==========

    @Test
    @DisplayName("Valid price should pass validation")
    void testValidPrice() {
        assertDoesNotThrow(() -> ValidationUtils.validatePositivePrice(10.99, "Price"));
        assertDoesNotThrow(() -> ValidationUtils.validatePositivePrice(0.0, "Price"));
        assertDoesNotThrow(() -> ValidationUtils.validatePositivePrice(999.99, "Price"));
    }

    @Test
    @DisplayName("Negative price should throw ValidationException")
    void testNegativePrice() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validatePositivePrice(-5.99, "Price"));
        assertTrue(ex.getMessage().contains("cannot be negative"));
    }

    @Test
    @DisplayName("Excessive price should throw ValidationException")
    void testExcessivePrice() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validatePositivePrice(9999999.99, "Price"));
        assertTrue(ex.getMessage().contains("exceeds maximum"));
    }

    // ========== Quantity Validation Tests ==========

    @Test
    @DisplayName("Valid quantity should pass validation")
    void testValidQuantity() {
        assertDoesNotThrow(() -> ValidationUtils.validatePositiveQuantity(1, "Quantity"));
        assertDoesNotThrow(() -> ValidationUtils.validatePositiveQuantity(50, "Quantity"));
        assertDoesNotThrow(() -> ValidationUtils.validatePositiveQuantity(999, "Quantity"));
    }

    @Test
    @DisplayName("Zero or negative quantity should throw ValidationException")
    void testZeroOrNegativeQuantity() {
        ValidationException ex1 = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validatePositiveQuantity(0, "Quantity"));
        assertTrue(ex1.getMessage().contains("must be greater than zero"));

        ValidationException ex2 = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validatePositiveQuantity(-5, "Quantity"));
        assertTrue(ex2.getMessage().contains("must be greater than zero"));
    }

    @Test
    @DisplayName("Excessive quantity should throw ValidationException")
    void testExcessiveQuantity() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validatePositiveQuantity(1000, "Quantity"));
        assertTrue(ex.getMessage().contains("exceeds maximum"));
    }

    // ========== Rating Validation Tests ==========

    @Test
    @DisplayName("Valid rating should pass validation")
    void testValidRating() {
        assertDoesNotThrow(() -> ValidationUtils.validateRating(0));
        assertDoesNotThrow(() -> ValidationUtils.validateRating(3));
        assertDoesNotThrow(() -> ValidationUtils.validateRating(5));
    }

    @Test
    @DisplayName("Invalid rating should throw ValidationException")
    void testInvalidRating() {
        ValidationException ex1 = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateRating(-1));
        assertTrue(ex1.getMessage().contains("between 0 and 5"));

        ValidationException ex2 = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateRating(6));
        assertTrue(ex2.getMessage().contains("between 0 and 5"));
    }

    // ========== String Length Validation Tests ==========

    @Test
    @DisplayName("Valid string length should pass validation")
    void testValidStringLength() {
        assertDoesNotThrow(() -> 
            ValidationUtils.validateLength("Hello", "Name", 3, 10));
        assertDoesNotThrow(() -> 
            ValidationUtils.validateLength("Test", "Name", 4, 4));
    }

    @Test
    @DisplayName("Short string should throw ValidationException")
    void testShortString() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateLength("Hi", "Name", 3, 10));
        assertTrue(ex.getMessage().contains("at least 3 characters"));
    }

    @Test
    @DisplayName("Long string should throw ValidationException")
    void testLongString() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateLength("Very long name", "Name", 3, 10));
        assertTrue(ex.getMessage().contains("cannot exceed 10 characters"));
    }

    // ========== Not Empty Validation Tests ==========

    @Test
    @DisplayName("Valid non-empty string should pass validation")
    void testValidNonEmptyString() {
        assertDoesNotThrow(() -> ValidationUtils.validateNotEmpty("Hello", "Field"));
        assertDoesNotThrow(() -> ValidationUtils.validateNotEmpty("  test  ", "Field"));
    }

    @Test
    @DisplayName("Empty string should throw ValidationException")
    void testEmptyString() {
        ValidationException ex1 = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateNotEmpty("", "Field"));
        assertTrue(ex1.getMessage().contains("cannot be empty"));

        ValidationException ex2 = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateNotEmpty("   ", "Field"));
        assertTrue(ex2.getMessage().contains("cannot be empty"));

        ValidationException ex3 = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateNotEmpty(null, "Field"));
        assertTrue(ex3.getMessage().contains("cannot be empty"));
    }

    // ========== ID Validation Tests ==========

    @Test
    @DisplayName("Valid positive ID should pass validation")
    void testValidPositiveId() {
        assertDoesNotThrow(() -> ValidationUtils.validatePositiveId(0, "User"));
        assertDoesNotThrow(() -> ValidationUtils.validatePositiveId(123, "User"));
    }

    @Test
    @DisplayName("Negative ID should throw ValidationException")
    void testNegativeId() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validatePositiveId(-1, "User"));
        assertTrue(ex.getMessage().contains("cannot be negative"));
    }
}
