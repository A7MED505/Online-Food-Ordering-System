package com.foodordering.ui;

import com.foodordering.dao.OrderDAO;
import com.foodordering.dao.UserDAO;
import com.foodordering.models.Order;
import com.foodordering.models.User;
import com.foodordering.utils.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;


public class ProfileFrame extends JFrame {
    private final User currentUser;
    private final UserDAO userDAO;
    private final OrderDAO orderDAO;

    // User info fields
    private final JTextField usernameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField addressField = new JTextField(20);

    // Order history table
    private final String[] columns = {"Order ID", "Restaurant", "Total", "Status", "Date"};
    private final DefaultTableModel orderTableModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable orderTable = new JTable(orderTableModel);

    public ProfileFrame(User currentUser, UserDAO userDAO, OrderDAO orderDAO) {
        this.currentUser = currentUser;
        this.userDAO = userDAO;
        this.orderDAO = orderDAO;
        initializeUI();
        loadUserInfo();
        loadOrderHistory();
    }

    private void initializeUI() {
        setTitle("My Profile - " + currentUser.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top panel: User Information
        JPanel userInfoPanel = new JPanel(new GridBagLayout());
        userInfoPanel.setBorder(BorderFactory.createTitledBorder("User Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Username (read-only)
        gbc.gridx = 0;
        gbc.gridy = 0;
        userInfoPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField.setEditable(false);
        usernameField.setBackground(Color.LIGHT_GRAY);
        userInfoPanel.add(usernameField, gbc);

        // Email (read-only)
        gbc.gridx = 0;
        gbc.gridy = 1;
        userInfoPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField.setEditable(false);
        emailField.setBackground(Color.LIGHT_GRAY);
        userInfoPanel.add(emailField, gbc);

        // Phone (editable)
        gbc.gridx = 0;
        gbc.gridy = 2;
        userInfoPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        userInfoPanel.add(phoneField, gbc);

        // Address (editable)
        gbc.gridx = 0;
        gbc.gridy = 3;
        userInfoPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        userInfoPanel.add(addressField, gbc);

        // Update button
        gbc.gridx = 1;
        gbc.gridy = 4;
        JButton updateButton = new JButton("Update Profile");
        updateButton.addActionListener(e -> updateProfile());
        userInfoPanel.add(updateButton, gbc);

        // Center panel: Order History
        JPanel orderHistoryPanel = new JPanel(new BorderLayout());
        orderHistoryPanel.setBorder(BorderFactory.createTitledBorder("Order History"));
        orderHistoryPanel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        // Add panels to frame
        add(userInfoPanel, BorderLayout.NORTH);
        add(orderHistoryPanel, BorderLayout.CENTER);

        // Bottom panel: Close button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadUserInfo() {
        usernameField.setText(currentUser.getUsername());
        emailField.setText(currentUser.getEmail());

        // Load customer details (phone, address)
        int customerId = getCustomerId(currentUser.getId());
        if (customerId > 0) {
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "SELECT phone, address FROM customers WHERE customer_id = ?")) {
                ps.setInt(1, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        phoneField.setText(rs.getString("phone") != null ? rs.getString("phone") : "");
                        addressField.setText(rs.getString("address") != null ? rs.getString("address") : "");
                    }
                }
            } catch (SQLException ex) {
                System.err.println("Error loading customer info: " + ex.getMessage());
            }
        }
    }

    public void loadOrderHistory() {
        orderTableModel.setRowCount(0);
        int customerId = getCustomerId(currentUser.getId());
        if (customerId > 0) {
            List<Order> orders = orderDAO.getOrdersByCustomerId(customerId);
            for (Order order : orders) {
                String restaurantName = getRestaurantName(order.getRestaurantId());
                orderTableModel.addRow(new Object[]{
                        order.getOrderId(),
                        restaurantName,
                        String.format("$%.2f", order.getTotalPrice()),
                        order.getStatus(),
                        "N/A"
                });
            }
        }
    }

    private void updateProfile() {
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        int customerId = getCustomerId(currentUser.getId());
        if (customerId > 0) {
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE customers SET phone = ?, address = ? WHERE customer_id = ?")) {
                ps.setString(1, phone.isEmpty() ? null : phone);
                ps.setString(2, address.isEmpty() ? null : address);
                ps.setInt(3, customerId);
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                System.err.println("Error updating profile: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Error updating profile: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Customer record not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getCustomerId(int userId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT customer_id FROM customers WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("customer_id");
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error getting customer ID: " + ex.getMessage());
        }
        return 0;
    }

    private String getRestaurantName(int restaurantId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT name FROM restaurants WHERE restaurant_id = ?")) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error getting restaurant name: " + ex.getMessage());
        }
        return "Unknown";
    }

    // Testing helpers
    public String getPhoneText() {
        return phoneField.getText();
    }

    public String getAddressText() {
        return addressField.getText();
    }

    public int getOrderHistoryRowCount() {
        return orderTableModel.getRowCount();
    }

    public DefaultTableModel getOrderTableModel() {
        return orderTableModel;
    }
}
