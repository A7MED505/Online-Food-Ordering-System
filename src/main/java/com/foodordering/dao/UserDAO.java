package com.foodordering.dao;

import com.foodordering.models.User;
import com.foodordering.utils.DatabaseConnection;

import java.sql.*;

/**
 * Data Access Object for User operations.
 * Handles user registration, login, and CRUD operations.
 */
public class UserDAO {

    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Register a new user in the database.
     * @param user User object with username, email, and password
     * @return true if registration successful, false otherwise
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, user_type) VALUES (?, ?, ?, 'customer')";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Username or email already exists: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        
        return false;
    }

    /**
     * Authenticate user with username and password.
     * @param username username
     * @param rawPassword plain password
     * @return User object if login successful, null otherwise
     */
    public User login(String username, String rawPassword) {
        String sql = "SELECT user_id, username, email, password_hash FROM users WHERE username = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("user_id");
                String email = rs.getString("email");
                String storedHash = rs.getString("password_hash");
                
                User user = new User(id, username, email, "");
                user.setPassword(rawPassword);
                
                if (user.getPasswordHash().equals(storedHash)) {
                    return user;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Get user by ID.
     * @param userId user ID
     * @return User object or null
     */
    public User getUserById(int userId) {
        String sql = "SELECT user_id, username, email, password_hash FROM users WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("user_id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String passwordHash = rs.getString("password_hash");
                
                User user = new User(id, username, email, "");
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Check if username already exists.
     * @param username username to check
     * @return true if exists, false otherwise
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        
        return false;
    }
}
