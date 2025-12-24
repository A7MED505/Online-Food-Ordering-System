package com.foodordering;

import com.foodordering.ui.LoginFrame;
import javax.swing.SwingUtilities;

/**
 * Main entry point for the Online Food Ordering System application.
 * 
 * @author A7MED505
 * @version 1.0.0
 */
public class Main {
    
    public static void main(String[] args) {
        // Initialize and show login screen
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
