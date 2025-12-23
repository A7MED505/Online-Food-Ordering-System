package com.foodordering.ui;

import com.foodordering.dao.UserDAO;
import com.foodordering.models.User;
import com.foodordering.utils.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for RegisterFrame.
 * Tests user registration flow with validation.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegisterFrameTest {

    private static UserDAO userDAO;
    private static Connection connection;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        connection = DatabaseConnection.getInstance().getConnection();
        userDAO = new UserDAO();
        
        // Clean up test data
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM users WHERE username LIKE 'reg_test%'");
        stmt.close();
    }

    @AfterEach
    void cleanUpAfterEach() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM users WHERE username LIKE 'reg_test%'");
        stmt.close();
    }

    @Test
    @Order(1)
    @DisplayName("Test successful registration")
    void testSuccessfulRegistration() {
        User user = new User(0, "reg_test_user", "reg_test@email.com", "SecurePass123");
        
        boolean result = userDAO.registerUser(user);
        
        assertTrue(result, "Registration should succeed with valid data");
        assertTrue(user.getId() > 0, "User should have ID after registration");
        
        // Verify user can login
        User loggedIn = userDAO.login("reg_test_user", "SecurePass123");
        assertNotNull(loggedIn, "Registered user should be able to login");
        assertEquals("reg_test@email.com", loggedIn.getEmail());
    }

    @Test
    @Order(2)
    @DisplayName("Test validation - empty username")
    void testValidationEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User(0, "", "test@email.com", "password");
        }, "Username should not be empty");
    }

    @Test
    @Order(3)
    @DisplayName("Test validation - invalid email")
    void testValidationInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User(0, "username", "invalidemail", "password");
        }, "Email should be valid");
    }

    @Test
    @Order(4)
    @DisplayName("Test validation - weak password")
    void testValidationWeakPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User(0, "username", "test@email.com", "123");
        }, "Password should be at least 8 characters");
    }

    @Test
    @Order(5)
    @DisplayName("Test duplicate username rejection")
    void testDuplicateUsernameRejection() {
        User user1 = new User(0, "reg_test_dup", "first@email.com", "Password123");
        userDAO.registerUser(user1);
        
        User user2 = new User(0, "reg_test_dup", "second@email.com", "Password456");
        boolean result = userDAO.registerUser(user2);
        
        assertFalse(result, "Registration with duplicate username should fail");
    }
}
