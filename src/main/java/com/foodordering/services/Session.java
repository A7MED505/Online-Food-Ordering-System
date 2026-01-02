package com.foodordering.services;

import com.foodordering.models.User;

/**
 * Session management for logged-in users using Singleton pattern.
 * Maintains current user state across the application.
 */
public class Session {
    
    private static Session instance;
    private User currentUser;
    
    private Session() {
    }
    
    /** @return Singleton instance */
    public static synchronized Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }
    
    /** Sets current logged-in user */
    public void login(User user) {
        this.currentUser = user;
    }
    
    /** Clears current user session */
    public void logout() {
        this.currentUser = null;
    }
    
    /** @return true if user is logged in */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /** @return Current logged-in user or null */
    public User getCurrentUser() {
        return currentUser;
    }
}
