package com.foodordering.services;

import com.foodordering.models.User;

/**
 * Session management for logged-in users.
 * Implements singleton pattern to maintain user session.
 */
public class Session {
    
    private static Session instance;
    private User currentUser;
    
    private Session() {
    }
    
    public static synchronized Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }
    
    public void login(User user) {
        this.currentUser = user;
    }
    
    public void logout() {
        this.currentUser = null;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
}
