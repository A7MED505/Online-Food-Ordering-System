package com.foodordering.utils;

import com.foodordering.exceptions.*;

import javax.swing.*;
import java.sql.SQLException;

/**
 * Centralized exception handler for the application.
 * Provides user-friendly error messages and logging.
 */
public class ExceptionHandler {

    /**
     * Handles exceptions and displays appropriate user-friendly messages.
     * @param e The exception to handle
     * @param parentComponent Parent component for JOptionPane (can be null)
     */
    public static void handleException(Exception e, java.awt.Component parentComponent) {
        String userMessage;
        String title;

        if (e instanceof ValidationException) {
            userMessage = e.getMessage();
            title = "Validation Error";
            logError("Validation error: " + e.getMessage());
        } else if (e instanceof UserAlreadyExistsException) {
            userMessage = e.getMessage();
            title = "Registration Error";
            logError("User registration failed: " + e.getMessage());
        } else if (e instanceof AuthenticationException) {
            userMessage = "Invalid username or password. Please try again.";
            title = "Login Failed";
            logError("Authentication failed: " + e.getMessage());
        } else if (e instanceof ResourceNotFoundException) {
            userMessage = e.getMessage();
            title = "Not Found";
            logError("Resource not found: " + e.getMessage());
        } else if (e instanceof PaymentException) {
            userMessage = "Payment processing failed: " + e.getMessage();
            title = "Payment Error";
            logError("Payment error: " + e.getMessage());
        } else if (e instanceof OrderException) {
            userMessage = "Order error: " + e.getMessage();
            title = "Order Error";
            logError("Order processing failed: " + e.getMessage());
        } else if (e instanceof DatabaseException) {
            userMessage = "A database error occurred. Please try again later.";
            title = "System Error";
            logError("Database error: " + e.getMessage(), e);
        } else if (e instanceof SQLException) {
            userMessage = "A database error occurred. Please contact support.";
            title = "System Error";
            logError("SQL error: " + e.getMessage(), e);
        } else {
            userMessage = "An unexpected error occurred: " + e.getMessage();
            title = "Error";
            logError("Unexpected error: " + e.getMessage(), e);
        }

        // Display error message to user
        if (parentComponent != null) {
            JOptionPane.showMessageDialog(
                parentComponent,
                userMessage,
                title,
                JOptionPane.ERROR_MESSAGE
            );
        } else {
            System.err.println(title + ": " + userMessage);
        }
    }

    /**
     * Logs error message to console (in production, this would use a logging framework).
     * @param message Error message
     */
    private static void logError(String message) {
        System.err.println("[ERROR] " + java.time.LocalDateTime.now() + " - " + message);
    }

    /**
     * Logs error message with exception stack trace.
     * @param message Error message
     * @param e Exception
     */
    private static void logError(String message, Exception e) {
        System.err.println("[ERROR] " + java.time.LocalDateTime.now() + " - " + message);
        e.printStackTrace();
    }

    /**
     * Wraps SQLException into DatabaseException.
     * @param message Custom error message
     * @param e Original SQLException
     * @return DatabaseException
     */
    public static DatabaseException wrapSQLException(String message, SQLException e) {
        return new DatabaseException(message + ": " + e.getMessage(), e);
    }

    /**
     * Checks for SQL constraint violations and throws appropriate exceptions.
     * @param e SQLException to check
     * @throws UserAlreadyExistsException if unique constraint violation
     * @throws DatabaseException for other SQL errors
     */
    public static void handleSQLException(SQLException e) throws UserAlreadyExistsException, DatabaseException {
        String sqlState = e.getSQLState();
        int errorCode = e.getErrorCode();

        // MySQL duplicate entry error
        if ("23000".equals(sqlState) || errorCode == 1062) {
            throw new UserAlreadyExistsException("Username or email already exists");
        }

        // Foreign key constraint violation
        if (errorCode == 1452) {
            throw new DatabaseException("Referenced record does not exist", e);
        }

        // General database error
        throw new DatabaseException("Database operation failed: " + e.getMessage(), e);
    }
}
