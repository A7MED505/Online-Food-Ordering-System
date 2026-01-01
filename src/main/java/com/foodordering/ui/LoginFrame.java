package com.foodordering.ui;

import com.foodordering.exceptions.AuthenticationException;
import com.foodordering.exceptions.DatabaseException;
import com.foodordering.exceptions.ValidationException;
import com.foodordering.models.User;
import com.foodordering.services.Session;
import com.foodordering.services.UserService;

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
    private UserService userService;

    public LoginFrame() {
        this.userService = new UserService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Login - Online Food Ordering System");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Form panel (username/password)
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        // Message panel (spans full width)
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegisterFrame());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // Compose main layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(messagePanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required");
            return;
        }

        try {
            User user = userService.loginUser(username, password);
            Session.getInstance().login(user);
            showSuccess("Login successful! Welcome, " + user.getUsername());
            clearFields();

            // Open main application window
            MainAppFrame appFrame = new MainAppFrame(user);
            appFrame.setVisible(true);
            // Close login window
            dispose();
        } catch (ValidationException | AuthenticationException e) {
            showError(e.getMessage());
            JOptionPane.showMessageDialog(this, e.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        } catch (DatabaseException e) {
            showError("A system error occurred. Please try again later.");
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
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
        messageLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        messageLabel.setForeground(new Color(0, 128, 0));
        messageLabel.setText(message);
        messageLabel.setVisible(true);
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
