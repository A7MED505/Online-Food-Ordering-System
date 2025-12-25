package com.foodordering.ui;

import com.foodordering.dao.UserDAO;
import com.foodordering.models.User;
import com.foodordering.services.Session;

import javax.swing.*;
import java.awt.*;

/**
 * Login UI Frame for user authentication.
 * Manages user login and session creation.
 */
public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;
    private UserDAO userDAO;

    public LoginFrame() {
        this.userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Login - Online Food Ordering System");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Username
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        // Password
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        // Message label
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        panel.add(messageLabel);
        panel.add(new JLabel("")); // Empty space

        // Buttons
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegisterFrame());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required");
            return;
        }

        User user = userDAO.login(username, password);

        if (user != null) {
            Session.getInstance().login(user);
            showSuccess("Login successful! Welcome, " + user.getUsername());
            clearFields();

            // Open main application window
            MainAppFrame appFrame = new MainAppFrame(user);
            appFrame.setVisible(true);
            // Close login window
            dispose();
        } else {
            showError("Invalid username or password");
        }
    }

    private void openRegisterFrame() {
        RegisterFrame registerFrame = new RegisterFrame();
        registerFrame.setVisible(true);
        // Don't close login frame, just hide it
        this.setVisible(false);
    }

    private void showError(String message) {
        messageLabel.setForeground(Color.RED);
        messageLabel.setText(message);
    }

    private void showSuccess(String message) {
        messageLabel.setForeground(new Color(0, 128, 0));
        messageLabel.setText(message);
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }

    // For testing purposes
    public void setUsername(String username) {
        usernameField.setText(username);
    }

    public void setPassword(String password) {
        passwordField.setText(password);
    }

    public String getMessageText() {
        return messageLabel.getText();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
