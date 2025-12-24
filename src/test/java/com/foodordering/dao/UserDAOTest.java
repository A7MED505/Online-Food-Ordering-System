package com.foodordering.dao;

import com.foodordering.models.User;
import com.foodordering.utils.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDAOTest {

    private static UserDAO userDAO;
    private static Connection connection;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        connection = DatabaseConnection.getInstance().getConnection();
        userDAO = new UserDAO();
        
        // Clean up test data
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM customers WHERE user_id IN (SELECT user_id FROM users WHERE username LIKE 'test%')");
        stmt.execute("DELETE FROM users WHERE username LIKE 'test%'");
        stmt.close();
    }

    @AfterEach
    void cleanUpAfterEach() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM customers WHERE user_id IN (SELECT user_id FROM users WHERE username LIKE 'test%')");
        stmt.execute("DELETE FROM users WHERE username LIKE 'test%'");
        stmt.close();
    }

    @Test
    @Order(1)
    @DisplayName("Test user registration")
    void testRegisterUser() {
        User user = new User(0, "testuser1", "test1@email.com", "password123");
        boolean result = userDAO.registerUser(user);
        
        assertTrue(result, "User registration should succeed");
        assertTrue(user.getId() > 0, "User ID should be set after registration");
    }

    @Test
    @Order(2)
    @DisplayName("Test duplicate username registration")
    void testDuplicateUsername() {
        User user1 = new User(0, "testuser2", "test2@email.com", "password123");
        userDAO.registerUser(user1);
        
        User user2 = new User(0, "testuser2", "different@email.com", "password456");
        boolean result = userDAO.registerUser(user2);
        
        assertFalse(result, "Registration with duplicate username should fail");
    }

    @Test
    @Order(3)
    @DisplayName("Test successful login")
    void testLoginSuccess() {
        User user = new User(0, "testuser3", "test3@email.com", "mypassword");
        userDAO.registerUser(user);
        
        User loggedIn = userDAO.login("testuser3", "mypassword");
        
        assertNotNull(loggedIn, "Login should succeed with correct credentials");
        assertEquals("testuser3", loggedIn.getUsername());
        assertEquals("test3@email.com", loggedIn.getEmail());
    }

    @Test
    @Order(4)
    @DisplayName("Test failed login with wrong password")
    void testLoginFailure() {
        User user = new User(0, "testuser4", "test4@email.com", "correctpass");
        userDAO.registerUser(user);
        
        User result = userDAO.login("testuser4", "wrongpass");
        
        assertNull(result, "Login should fail with incorrect password");
    }
}
