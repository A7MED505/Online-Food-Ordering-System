package com.foodordering.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database Connection Manager using Singleton Pattern.
 * Manages MySQL database connections for the application.
 * 
 * @author A7MED505
 * @version 1.0.0
 */
public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private Connection connection;
    private String url;
    private String username;
    private String password;
    private String driver;
    
    /**
     * Private constructor - implements Singleton pattern
     */
    private DatabaseConnection() {
        loadProperties();
        connectToDatabase();
    }
    
    /**
     * Get singleton instance of DatabaseConnection
     * @return DatabaseConnection instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Load database configuration from properties file
     */
    private void loadProperties() {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            
            if (input == null) {
                System.err.println("ERROR: database.properties file not found!");
                System.err.println("Please create src/main/resources/database.properties with your MySQL credentials");
                throw new IOException("database.properties not found");
            }
            
            props.load(input);
            
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
            this.driver = props.getProperty("db.driver");
            
            if (url == null || username == null || password == null || driver == null) {
                throw new IOException("Missing required database configuration properties");
            }
            
            System.out.println("✓ Database properties loaded successfully");
            System.out.println("  URL: " + url);
            System.out.println("  Username: " + username);
            
        } catch (IOException e) {
            System.err.println("ERROR loading database.properties: " + e.getMessage());
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }
    
    /**
     * Establish connection to the database
     */
    private void connectToDatabase() {
        try {
            Class.forName(driver);
            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("✓ Database connection established successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC driver not found: " + e.getMessage());
            throw new RuntimeException("MySQL JDBC driver not found. Add mysql-connector-java to Maven dependencies", e);
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to connect to database: " + e.getMessage());
            System.err.println("  Check your MySQL server is running");
            System.err.println("  Verify database.properties credentials");
            System.err.println("  Verify database 'food_ordering_system' exists");
            throw new RuntimeException("Failed to establish database connection", e);
        }
    }
    
    /**
     * Get active database connection
     * @return Connection object
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connectToDatabase();
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Connection is closed, attempting to reconnect: " + e.getMessage());
            connectToDatabase();
        }
        return connection;
    }
    
    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to close connection: " + e.getMessage());
        }
    }
    
    /**
     * Test database connection without returning data
     * Useful for health checks
     * @return true if connection is valid, false otherwise
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed() && conn.isValid(5);
        } catch (SQLException e) {
            System.err.println("ERROR: Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
