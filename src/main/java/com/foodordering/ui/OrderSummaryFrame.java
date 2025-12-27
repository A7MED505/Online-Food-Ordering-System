package com.foodordering.ui;

import com.foodordering.dao.OrderDAO;
import com.foodordering.models.Order;
import com.foodordering.models.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Order Summary UI to display order confirmation and details.
 */
public class OrderSummaryFrame extends JFrame {
    private final OrderDAO orderDAO;
    private Order order;

    private final String[] columns = {"Item", "Unit Price", "Qty", "Subtotal"};
    private final DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable itemsTable = new JTable(tableModel);
    private final JLabel orderIdLabel = new JLabel();
    private final JLabel statusLabel = new JLabel();
    private final JLabel totalLabel = new JLabel();
    private final JLabel dateLabel = new JLabel();
    private final JTextArea confirmationArea = new JTextArea();

    public OrderSummaryFrame(int orderId, OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
        this.order = orderDAO.getOrderById(orderId);
        initializeUI();
        loadOrderDetails();
    }

    // For testing: inject order directly
    public OrderSummaryFrame(Order order, OrderDAO orderDAO) {
        this.order = order;
        this.orderDAO = orderDAO;
        initializeUI();
        loadOrderDetails();
    }

    private void initializeUI() {
        setTitle("Order Summary");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top panel: order info
        JPanel topPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        orderIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));

        topPanel.add(orderIdLabel);
        topPanel.add(statusLabel);
        topPanel.add(dateLabel);
        topPanel.add(totalLabel);

        // Center: items table
        JScrollPane scrollPane = new JScrollPane(itemsTable);

        // Bottom: confirmation message + close button
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        confirmationArea.setEditable(false);
        confirmationArea.setLineWrap(true);
        confirmationArea.setWrapStyleWord(true);
        confirmationArea.setFont(new Font("Arial", Font.PLAIN, 12));
        confirmationArea.setBackground(new Color(240, 255, 240));
        confirmationArea.setBorder(BorderFactory.createLineBorder(new Color(0, 128, 0), 2));
        confirmationArea.setPreferredSize(new Dimension(600, 60));

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        bottomPanel.add(confirmationArea, BorderLayout.CENTER);
        bottomPanel.add(closeButton, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadOrderDetails() {
        if (order == null) {
            orderIdLabel.setText("Order not found!");
            return;
        }

        orderIdLabel.setText("Order ID: #" + order.getOrderId());
        statusLabel.setText("Status: " + order.getStatus().toUpperCase());
        totalLabel.setText(String.format("Total: $%.2f", order.getTotalPrice()));

        // Format current date as order date (simplified for demo)
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        dateLabel.setText("Order Date: " + dateStr);

        // Load items
        tableModel.setRowCount(0);
        for (OrderItem item : order.getItems()) {
            double subtotal = item.getUnitPrice() * item.getQuantity();
            tableModel.addRow(new Object[]{
                "Item #" + item.getItemId(),
                String.format("$%.2f", item.getUnitPrice()),
                item.getQuantity(),
                String.format("$%.2f", subtotal)
            });
        }

        // Confirmation message
        confirmationArea.setText(
            "✅ Thank you! Your order has been placed successfully.\n" +
            "Order #" + order.getOrderId() + " is being prepared. You will receive updates soon."
        );
    }

    // Testing helpers
    public String getOrderIdText() {
        return orderIdLabel.getText();
    }

    public String getStatusText() {
        return statusLabel.getText();
    }

    public String getTotalText() {
        return totalLabel.getText();
    }

    public String getConfirmationText() {
        return confirmationArea.getText();
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public Order getOrder() {
        return order;
    }
}
