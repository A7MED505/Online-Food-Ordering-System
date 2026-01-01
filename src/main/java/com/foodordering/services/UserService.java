package com.foodordering.services;

import com.foodordering.dao.UserDAO;
import com.foodordering.exceptions.*;
import com.foodordering.models.User;
import com.foodordering.utils.ValidationUtils;

/**
 * Service layer for user operations with proper exception handling.
 * Demonstrates best practices for validation and error handling.
 */
public class UserService {
    
    private final UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    /**
     * Register a new user with validation and exception handling.
     * 
     * @param username Username for the new user
     * @param email Email address
     * @param password Plain text password
     * @return The registered User object
     * @throws ValidationException if input validation fails
     * @throws UserAlreadyExistsException if username or email already exists
     * @throws DatabaseException if database operation fails
     */
    public User registerUser(String username, String email, String password) 
            throws ValidationException, UserAlreadyExistsException, DatabaseException {
        
        // Step 1: Validate inputs
        try {
            ValidationUtils.validateUsername(username);
            ValidationUtils.validateEmail(email);
            ValidationUtils.validatePassword(password);
        } catch (ValidationException e) {
            throw e; // Re-throw validation exceptions
        }
        
        // Step 2: Create user object
        User newUser;
        try {
            newUser = new User(0, username, email, password);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid user data: " + e.getMessage(), e);
        }
        
        // Step 3: Register in database
        try {
            boolean success = userDAO.registerUser(newUser);
            if (!success) {
                throw new DatabaseException("Failed to register user - operation returned false");
            }
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                throw new UserAlreadyExistsException(
                    "Username '" + username + "' or email '" + email + "' is already registered", e);
            }
            throw new DatabaseException("Database error during user registration", e);
        }
        
        return newUser;
    }
    
    /**
     * Authenticate user with exception handling.
     * 
     * @param username Username
     * @param password Plain text password
     * @return The authenticated User object
     * @throws ValidationException if inputs are invalid
     * @throws AuthenticationException if credentials are invalid
     * @throws DatabaseException if database operation fails
     */
    public User loginUser(String username, String password) 
            throws ValidationException, AuthenticationException, DatabaseException {
        
        // Step 1: Validate inputs
        try {
            ValidationUtils.validateNotEmpty(username, "Username");
            ValidationUtils.validateNotEmpty(password, "Password");
        } catch (ValidationException e) {
            throw e;
        }
        
        // Step 2: Attempt authentication
        User user;
        try {
            user = userDAO.login(username, password);
        } catch (Exception e) {
            throw new DatabaseException("Database error during login", e);
        }
        
        // Step 3: Check authentication result
        if (user == null) {
            throw new AuthenticationException(
                "Invalid username or password for user: " + username);
        }
        
        return user;
    }
    
    /**
     * Get user by ID with exception handling.
     * 
     * @param userId User ID to retrieve
     * @return The User object
     * @throws ValidationException if user ID is invalid
     * @throws ResourceNotFoundException if user not found
     * @throws DatabaseException if database operation fails
     */
    public User getUserById(int userId) 
            throws ValidationException, ResourceNotFoundException, DatabaseException {
        
        // Validate input
        if (userId <= 0) {
            throw new ValidationException("User ID must be positive, got: " + userId);
        }
        
        // Retrieve user
        User user;
        try {
            user = userDAO.getUserById(userId);
        } catch (Exception e) {
            throw new DatabaseException("Database error retrieving user with ID: " + userId, e);
        }
        
        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        
        return user;
    }
    
    /**
     * Update user email with validation.
     * 
     * @param userId User ID
     * @param newEmail New email address
     * @throws ValidationException if email format is invalid
     * @throws ResourceNotFoundException if user not found
     * @throws UserAlreadyExistsException if email already used by another user
     * @throws DatabaseException if database operation fails
     */
    public void updateUserEmail(int userId, String newEmail) 
            throws ValidationException, ResourceNotFoundException, UserAlreadyExistsException, DatabaseException {
        
        // Validate inputs
        ValidationUtils.validatePositiveId(userId, "User");
        ValidationUtils.validateEmail(newEmail);
        
        // Check if user exists
        getUserById(userId); // Will throw ResourceNotFoundException if not found
        
        // Update email
        try {
            boolean success = userDAO.updateUserEmail(userId, newEmail);
            if (!success) {
                throw new DatabaseException("Failed to update email - operation returned false");
            }
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                throw new UserAlreadyExistsException("Email '" + newEmail + "' is already in use", e);
            }
            throw new DatabaseException("Database error updating user email", e);
        }
    }
}
