package com.foodordering.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DatabaseConnection class.
 * Tests the database connectivity and configuration.
 */
class DatabaseConnectionTest {
    
    private DatabaseConnection dbConnection;
    
    @BeforeEach
    void setUp() {
        dbConnection = DatabaseConnection.getInstance();
    }
    
    @Test
    @DisplayName("Test successful database connection")
    void testConnectionSuccess() {
        assertNotNull(dbConnection, "DatabaseConnection instance should not be null");
        
        Connection connection = dbConnection.getConnection();
        assertNotNull(connection, "Connection should be established");
        
        try {
            assertFalse(connection.isClosed(), "Connection should be open");
        } catch (Exception e) {
            fail("Failed to check connection state: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Test connection closes properly")
    void testConnectionClosure() {
        Connection connection = dbConnection.getConnection();
        assertNotNull(connection, "Connection should be established");
        
        try {
            dbConnection.closeConnection();
            assertTrue(connection.isClosed(), "Connection should be closed after closeConnection()");
        } catch (Exception e) {
            fail("Failed during connection closure: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Test singleton pattern - same instance returned")
    void testSingletonPattern() {
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        DatabaseConnection instance2 = DatabaseConnection.getInstance();
        
        assertSame(instance1, instance2, "Both instances should be the same object (singleton pattern)");
    }
    
    @Test
    @DisplayName("Test connection is not null on first call")
    void testConnectionNotNull() {
        Connection connection = dbConnection.getConnection();
        assertNotNull(connection, "Initial connection should not be null");
    }
}
