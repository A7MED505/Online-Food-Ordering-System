package com.foodordering.ui;

import com.foodordering.models.User;

import javax.swing.*;
import java.awt.*;

/**
 * Simple main application window placeholder.
 * Shows a welcome message and provides a logout button to return to Login.
 */
public class MainAppFrame extends JFrame {
    private final User currentUser;

    public MainAppFrame(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Online Food Ordering - Home");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel welcome = new JLabel("Welcome, " + currentUser.getUsername(), SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(welcome, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            // Return to login screen
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        });

        JPanel bottom = new JPanel();
        bottom.add(logoutButton);
        panel.add(bottom, BorderLayout.SOUTH);

        add(panel);
    }
}
