package com.foodordering.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for custom exception classes.
 * Verifies exception creation, messages, and inheritance.
 */
@DisplayName("Custom Exceptions Tests")
public class ExceptionTest {

    @Test
    @DisplayName("Test BaseApplicationException creation")
    void testBaseApplicationException() {
        String message = "Base exception message";
        BaseApplicationException exception = new BaseApplicationException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Test BaseApplicationException with cause")
    void testBaseApplicationExceptionWithCause() {
        String message = "Base exception with cause";
        Throwable cause = new RuntimeException("Original cause");
        BaseApplicationException exception = new BaseApplicationException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Test DatabaseException creation")
    void testDatabaseException() {
        String message = "Database connection failed";
        DatabaseException exception = new DatabaseException(message);
        
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof BaseApplicationException);
    }

    @Test
    @DisplayName("Test DatabaseException with cause")
    void testDatabaseExceptionWithCause() {
        String message = "SQL execution error";
        Throwable cause = new java.sql.SQLException("Connection timeout");
        DatabaseException exception = new DatabaseException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof java.sql.SQLException);
    }

    @Test
    @DisplayName("Test ValidationException creation")
    void testValidationException() {
        String message = "Email format is invalid";
        ValidationException exception = new ValidationException(message);
        
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof BaseApplicationException);
    }

    @Test
    @DisplayName("Test UserAlreadyExistsException creation")
    void testUserAlreadyExistsException() {
        String message = "Username already taken";
        UserAlreadyExistsException exception = new UserAlreadyExistsException(message);
        
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof BaseApplicationException);
    }

    @Test
    @DisplayName("Test AuthenticationException creation")
    void testAuthenticationException() {
        String message = "Invalid credentials";
        AuthenticationException exception = new AuthenticationException(message);
        
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof BaseApplicationException);
    }

    @Test
    @DisplayName("Test ResourceNotFoundException creation")
    void testResourceNotFoundException() {
        String message = "User with ID 123 not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);
        
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof BaseApplicationException);
    }

    @Test
    @DisplayName("Test PaymentException creation")
    void testPaymentException() {
        String message = "Insufficient funds";
        PaymentException exception = new PaymentException(message);
        
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof BaseApplicationException);
    }

    @Test
    @DisplayName("Test OrderException creation")
    void testOrderException() {
        String message = "Cannot place empty order";
        OrderException exception = new OrderException(message);
        
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof BaseApplicationException);
    }

    @Test
    @DisplayName("Test exception inheritance chain")
    void testExceptionInheritance() {
        DatabaseException dbException = new DatabaseException("DB Error");
        
        assertTrue(dbException instanceof BaseApplicationException);
        assertTrue(dbException instanceof Exception);
        assertTrue(dbException instanceof Throwable);
    }

    @Test
    @DisplayName("Test exception can be thrown and caught")
    void testExceptionThrowAndCatch() {
        assertThrows(ValidationException.class, () -> {
            throw new ValidationException("Test validation error");
        });

        assertThrows(DatabaseException.class, () -> {
            throw new DatabaseException("Test database error");
        });

        assertThrows(AuthenticationException.class, () -> {
            throw new AuthenticationException("Test auth error");
        });
    }

    @Test
    @DisplayName("Test catching BaseApplicationException catches all custom exceptions")
    void testPolymorphicExceptionHandling() {
        try {
            throw new ValidationException("Validation failed");
        } catch (BaseApplicationException e) {
            assertEquals("Validation failed", e.getMessage());
        }

        try {
            throw new DatabaseException("Database failed");
        } catch (BaseApplicationException e) {
            assertEquals("Database failed", e.getMessage());
        }
    }
}
