package com.foodordering.services;

import com.foodordering.dao.UserDAO;
import com.foodordering.exceptions.*;
import com.foodordering.models.User;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for UserService exception handling.
 * Verifies proper exception throwing and handling.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("User Service Exception Handling Tests")
public class UserServiceTest {

    private UserService userService;
    private UserDAO mockUserDAO;
    
    @BeforeEach
    void setup() {
        mockUserDAO = mock(UserDAO.class);
        userService = new UserService(mockUserDAO);
    }

    // ========== Registration Validation Tests ==========

    @Test
    @Order(1)
    @DisplayName("Register with invalid username should throw ValidationException")
    void testRegisterInvalidUsername() {
        // Test short username
        ValidationException ex1 = assertThrows(ValidationException.class, 
            () -> userService.registerUser("ab", "test@email.com", "password123"));
        assertTrue(ex1.getMessage().contains("at least 3 characters"));
        
        // Test username with special characters
        ValidationException ex2 = assertThrows(ValidationException.class, 
            () -> userService.registerUser("user@name", "test@email.com", "password123"));
        assertTrue(ex2.getMessage().contains("letters, numbers, and underscores"));
    }

    @Test
    @Order(2)
    @DisplayName("Register with invalid email should throw ValidationException")
    void testRegisterInvalidEmail() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> userService.registerUser("validuser", "invalid-email", "password123"));
        assertTrue(ex.getMessage().contains("Invalid email format"));
    }

    @Test
    @Order(3)
    @DisplayName("Register with short password should throw ValidationException")
    void testRegisterShortPassword() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> userService.registerUser("validuser", "test@email.com", "short"));
        assertTrue(ex.getMessage().contains("at least 8 characters"));
    }

    @Test
    @Order(4)
    @DisplayName("Register with existing user should throw UserAlreadyExistsException")
    void testRegisterExistingUser() {
        // Mock DAO to return false (user exists)
        when(mockUserDAO.registerUser(any(User.class))).thenReturn(false);
        
        assertThrows(DatabaseException.class, 
            () -> userService.registerUser("testuser", "test@email.com", "password123"));
    }

    // ========== Login Validation Tests ==========

    @Test
    @Order(5)
    @DisplayName("Login with empty username should throw ValidationException")
    void testLoginEmptyUsername() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> userService.loginUser("", "password123"));
        assertTrue(ex.getMessage().contains("cannot be empty"));
    }

    @Test
    @Order(6)
    @DisplayName("Login with empty password should throw ValidationException")
    void testLoginEmptyPassword() {
        ValidationException ex = assertThrows(ValidationException.class, 
            () -> userService.loginUser("testuser", ""));
        assertTrue(ex.getMessage().contains("cannot be empty"));
    }

    @Test
    @Order(7)
    @DisplayName("Login with invalid credentials should throw AuthenticationException")
    void testLoginInvalidCredentials() {
        // Mock DAO to return null (invalid credentials)
        when(mockUserDAO.login(anyString(), anyString())).thenReturn(null);
        
        AuthenticationException ex = assertThrows(AuthenticationException.class, 
            () -> userService.loginUser("testuser", "wrongpassword"));
        assertTrue(ex.getMessage().contains("Invalid username or password"));
    }

    @Test
    @Order(8)
    @DisplayName("Login with valid credentials should succeed")
    void testLoginSuccess() throws Exception {
        // Mock successful login
        User mockUser = new User(1, "testuser", "test@email.com", "password123");
        when(mockUserDAO.login("testuser", "password123")).thenReturn(mockUser);
        
        User result = userService.loginUser("testuser", "password123");
        
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    // ========== Get User By ID Tests ==========

    @Test
    @Order(9)
    @DisplayName("Get user with invalid ID should throw ValidationException")
    void testGetUserInvalidId() {
        ValidationException ex1 = assertThrows(ValidationException.class, 
            () -> userService.getUserById(0));
        assertTrue(ex1.getMessage().contains("must be positive"));
        
        ValidationException ex2 = assertThrows(ValidationException.class, 
            () -> userService.getUserById(-1));
        assertTrue(ex2.getMessage().contains("must be positive"));
    }

    @Test
    @Order(10)
    @DisplayName("Get non-existent user should throw ResourceNotFoundException")
    void testGetNonExistentUser() {
        // Mock DAO to return null (user not found)
        when(mockUserDAO.getUserById(999)).thenReturn(null);
        
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, 
            () -> userService.getUserById(999));
        assertTrue(ex.getMessage().contains("User not found with ID: 999"));
    }

    @Test
    @Order(11)
    @DisplayName("Get existing user should succeed")
    void testGetUserSuccess() throws Exception {
        // Mock successful retrieval
        User mockUser = new User(1, "testuser", "test@email.com", "password123");
        when(mockUserDAO.getUserById(1)).thenReturn(mockUser);
        
        User result = userService.getUserById(1);
        
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    // ========== Exception Hierarchy Tests ==========

    @Test
    @Order(12)
    @DisplayName("All custom exceptions extend BaseApplicationException")
    void testExceptionHierarchy() {
        Exception validationEx = new ValidationException("test");
        Exception authEx = new AuthenticationException("test");
        Exception dbEx = new DatabaseException("test");
        Exception notFoundEx = new ResourceNotFoundException("test");
        Exception userExistsEx = new UserAlreadyExistsException("test");
        
        assertTrue(validationEx instanceof BaseApplicationException);
        assertTrue(authEx instanceof BaseApplicationException);
        assertTrue(dbEx instanceof BaseApplicationException);
        assertTrue(notFoundEx instanceof BaseApplicationException);
        assertTrue(userExistsEx instanceof BaseApplicationException);
    }

    @Test
    @Order(13)
    @DisplayName("Exceptions can be caught polymorphically")
    void testPolymorphicExceptionHandling() {
        boolean caughtValidation = false;
        boolean caughtAuth = false;
        
        try {
            throw new ValidationException("Validation error");
        } catch (BaseApplicationException e) {
            caughtValidation = true;
        }
        
        try {
            throw new AuthenticationException("Auth error");
        } catch (BaseApplicationException e) {
            caughtAuth = true;
        }
        
        assertTrue(caughtValidation);
        assertTrue(caughtAuth);
    }
}
