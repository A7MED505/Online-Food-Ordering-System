package com.foodordering.ui;

import com.foodordering.dao.OrderDAO;
import com.foodordering.models.Cart;
import com.foodordering.models.CartItem;
import com.foodordering.services.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * UI frame to display and manage shopping cart items.
 * Allows viewing, removing items, and updating quantities.
 */
public class CartFrame extends JFrame {
    private final Cart cart;

    private final String[] columns = {"Item Name", "Unit Price", "Quantity", "Subtotal"};
    private final DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 2; // Only quantity column is editable
        }
    };
    private final JTable cartTable = new JTable(tableModel);
    private final JLabel totalLabel = new JLabel("Total: $0.00");

    public CartFrame(Cart cart) {
        this.cart = cart;
        initializeUI();
        loadCartItems();
    }

    private void initializeUI() {
        setTitle("Shopping Cart");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JScrollPane scrollPane = new JScrollPane(cartTable);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalPanel.add(totalLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton updateButton = new JButton("Update Quantity");
        JButton removeButton = new JButton("Remove Item");
        JButton clearButton = new JButton("Clear Cart");
        JButton checkoutButton = new JButton("Checkout");

        updateButton.addActionListener(e -> updateSelectedQuantity());
        removeButton.addActionListener(e -> removeSelectedItem());
        clearButton.addActionListener(e -> clearCart());
        checkoutButton.addActionListener(e -> proceedToCheckout());

        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(checkoutButton);

        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void loadCartItems() {
        tableModel.setRowCount(0);
        List<CartItem> items = cart.getItems();
        for (CartItem item : items) {
            tableModel.addRow(new Object[]{
                item.getName(),
                String.format("$%.2f", item.getUnitPrice()),
                item.getQuantity(),
                String.format("$%.2f", item.getSubtotal())
            });
        }
        updateTotalLabel();
    }

    private void updateSelectedQuantity() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object quantityObj = tableModel.getValueAt(row, 2);
        if (!(quantityObj instanceof Integer)) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int newQuantity = (Integer) quantityObj;
        if (newQuantity <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<CartItem> items = cart.getItems();
        if (row < items.size()) {
            CartItem item = items.get(row);
            try {
                cart.updateQuantity(item.getItemId(), newQuantity);
                loadCartItems();
                JOptionPane.showMessageDialog(this, "Quantity updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeSelectedItem() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<CartItem> items = cart.getItems();
        if (row < items.size()) {
            CartItem item = items.get(row);
            cart.removeItem(item.getItemId());
            loadCartItems();
            JOptionPane.showMessageDialog(this, "Item removed from cart!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearCart() {
        int response = JOptionPane.showConfirmDialog(this, "Clear entire cart?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            cart.clearCart();
            loadCartItems();
            JOptionPane.showMessageDialog(this, "Cart cleared!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void proceedToCheckout() {
        if (cart.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty! Add items first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (!Session.getInstance().isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Please login to continue.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        CheckoutFrame checkout = new CheckoutFrame(Session.getInstance().getCurrentUser(), cart, new OrderDAO());
        checkout.setVisible(true);
    }

    private void updateTotalLabel() {
        totalLabel.setText(String.format("Total: $%.2f", cart.calculateTotal()));
    }

    // Testing helpers
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public Cart getCart() {
        return cart;
    }

    public String getTotalLabelText() {
        return totalLabel.getText();
    }
}
