package com.foodordering.ui;

import com.foodordering.dao.UserDAO;
import com.foodordering.models.User;
import com.foodordering.services.Session;
import com.foodordering.utils.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for LoginFrame.
 * Tests login functionality and session management.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginFrameTest {

    private static UserDAO userDAO;
    private static Connection connection;
    private static final String TEST_USERNAME = "login_test_user";
    private static final String TEST_PASSWORD = "TestPass123";
    private static final String TEST_EMAIL = "login@test.com";

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        connection = DatabaseConnection.getInstance().getConnection();
        userDAO = new UserDAO();
        
        // Clean up and create test user
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM users WHERE username = '" + TEST_USERNAME + "'");
        stmt.close();
        
        // Register test user
        User testUser = new User(0, TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);
        userDAO.registerUser(testUser);
    }

    @AfterAll
    static void cleanUpDatabase() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM users WHERE username = '" + TEST_USERNAME + "'");
        stmt.close();
    }

    @BeforeEach
    void setUp() {
        // Clear session before each test
        Session.getInstance().logout();
    }

    @Test
    @Order(1)
    @DisplayName("Test successful login")
    void testSuccessfulLogin() {
        User loggedInUser = userDAO.login(TEST_USERNAME, TEST_PASSWORD);
        
        assertNotNull(loggedInUser, "Login should succeed with correct credentials");
        assertEquals(TEST_USERNAME, loggedInUser.getUsername());
        assertEquals(TEST_EMAIL, loggedInUser.getEmail());
        
        // Test session management
        Session.getInstance().login(loggedInUser);
        assertTrue(Session.getInstance().isLoggedIn(), "User should be logged in");
        assertEquals(TEST_USERNAME, Session.getInstance().getCurrentUser().getUsername());
    }

    @Test
    @Order(2)
    @DisplayName("Test login with wrong password")
    void testInvalidCredentials() {
        User result = userDAO.login(TEST_USERNAME, "WrongPassword");
        
        assertNull(result, "Login should fail with incorrect password");
        assertFalse(Session.getInstance().isLoggedIn(), "Session should not be active");
    }

    @Test
    @Order(3)
    @DisplayName("Test login with non-existent user")
    void testNonExistentUser() {
        User result = userDAO.login("nonexistent_user", TEST_PASSWORD);
        
        assertNull(result, "Login should fail for non-existent user");
    }

    @Test
    @Order(4)
    @DisplayName("Test logout")
    void testLogout() {
        User loggedInUser = userDAO.login(TEST_USERNAME, TEST_PASSWORD);
        Session.getInstance().login(loggedInUser);
        
        assertTrue(Session.getInstance().isLoggedIn(), "User should be logged in initially");
        
        Session.getInstance().logout();
        
        assertFalse(Session.getInstance().isLoggedIn(), "User should be logged out");
        assertNull(Session.getInstance().getCurrentUser(), "Current user should be null");
    }
}
