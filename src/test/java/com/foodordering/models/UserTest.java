package com.foodordering.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1, "testuser", "test@email.com", "password123");
    }

    @Test
    @DisplayName("Test user creation")
    void testUserCreation() {
        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@email.com", user.getEmail());
    }

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        user.setUsername("newuser");
        user.setEmail("new@email.com");

        assertEquals("newuser", user.getUsername());
        assertEquals("new@email.com", user.getEmail());
    }

    @Test
    @DisplayName("Test password hashing and verification")
    void testPasswordEncryption() {
        String raw = "password123";
        User secureUser = new User(2, "secure", "secure@email.com", raw);

        assertNotEquals(raw, secureUser.getPasswordHash(), "Password should be stored hashed");
        assertTrue(secureUser.verifyPassword(raw), "verifyPassword should succeed for correct password");
        assertFalse(secureUser.verifyPassword("wrong"), "verifyPassword should fail for wrong password");
    }
}
