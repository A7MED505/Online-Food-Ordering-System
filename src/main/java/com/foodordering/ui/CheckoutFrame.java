package com.foodordering.ui;

import com.foodordering.dao.OrderDAO;
import com.foodordering.dao.CouponDAO;
import com.foodordering.interfaces.Orderable;
import com.foodordering.models.Cart;
import com.foodordering.models.CartItem;
import com.foodordering.models.Coupon;
import com.foodordering.models.Order;
import com.foodordering.models.OrderItem;
import com.foodordering.models.User;
import com.foodordering.payments.CashPayment;
import com.foodordering.payments.CreditCardPayment;
import com.foodordering.payments.DebitCardPayment;
import com.foodordering.services.PaymentService;
import com.foodordering.utils.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;


/**
 * Checkout UI to process payment and place order.
 */
public class CheckoutFrame extends JFrame {
    private final User currentUser;
    private final Cart cart;
    private final OrderDAO orderDAO;
    private final CouponDAO couponDAO = new CouponDAO();
    private final PaymentService paymentService = new PaymentService();

    private int lastOrderId;

    private final String[] columns = {"Item", "Unit Price", "Qty", "Subtotal"};
    private final DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable itemsTable = new JTable(tableModel);
    
    private final JLabel subtotalLabel = new JLabel("Subtotal: $0.00");
    private final JLabel discountLabel = new JLabel("Discount: $0.00");
    private final JLabel totalLabel = new JLabel("Total: $0.00");
    
    private final JTextField couponField = new JTextField(10);
    private final JButton applyCouponBtn = new JButton("Apply Coupon");
    
    private final JComboBox<String> paymentCombo = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "Cash"});

    public CheckoutFrame(User currentUser, Cart cart, OrderDAO orderDAO) {
        this.currentUser = currentUser;
        this.cart = cart;
        this.orderDAO = orderDAO;
        initializeUI();
        loadCartItems();
    }

    private void initializeUI() {
        setTitle("Checkout");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(new JScrollPane(itemsTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        
        // Summary panel with prices
        JPanel summary = new JPanel(new GridLayout(4, 1, 0, 5));
        summary.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        subtotalLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        discountLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        discountLabel.setForeground(new Color(0, 128, 0));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        summary.add(subtotalLabel);
        summary.add(discountLabel);
        summary.add(new JSeparator());
        summary.add(totalLabel);

        // Coupon panel
        JPanel couponPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        couponPanel.add(new JLabel("Coupon Code:"));
        couponPanel.add(couponField);
        applyCouponBtn.addActionListener(e -> applyCoupon());
        couponPanel.add(applyCouponBtn);

        // Payment method panel
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.add(new JLabel("Payment Method:"));
        actions.add(paymentCombo);
        JButton placeOrderBtn = new JButton("Place Order");
        placeOrderBtn.addActionListener(e -> placeOrderFromUI());
        actions.add(placeOrderBtn);

        bottom.add(summary, BorderLayout.NORTH);
        bottom.add(couponPanel, BorderLayout.CENTER);
        bottom.add(actions, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }

    private void applyCoupon() {
        String code = couponField.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a coupon code.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Coupon coupon = couponDAO.getCouponByCode(code);
        if (coupon == null || !coupon.isValid()) {
            JOptionPane.showMessageDialog(this, "Invalid or expired coupon code.", "Error", JOptionPane.ERROR_MESSAGE);
            couponField.setText("");
            cart.applyCoupon(null);
        } else {
            cart.applyCoupon(coupon);
            JOptionPane.showMessageDialog(this, "Coupon applied! " + (int)coupon.getValue() + "% discount.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        loadCartItems();
    }

    public void loadCartItems() {
        tableModel.setRowCount(0);
        for (CartItem ci : cart.getItems()) {
            tableModel.addRow(new Object[]{ci.getName(), ci.getUnitPrice(), ci.getQuantity(), ci.getSubtotal()});
        }
        
        // Calculate and display prices
        double subtotal = cart.getItems().stream().mapToDouble(CartItem::getSubtotal).sum();
        double discount = cart.getAppliedCoupon() != null ? cart.getAppliedCoupon().computeDiscount(subtotal) : 0;
        double total = subtotal - discount;
        
        subtotalLabel.setText(String.format("Subtotal: $%.2f", subtotal));
        discountLabel.setText(String.format("Discount: -$%.2f", discount));
        totalLabel.setText(String.format("Total: $%.2f", Math.max(0, total)));
    }

    private void placeOrderFromUI() {
        Orderable method = buildSelectedPaymentMethod();
        int orderId = placeOrderForTest(method);
        if (orderId > 0) {
            dispose();
            // Open order summary frame
            OrderSummaryFrame summaryFrame = new OrderSummaryFrame(orderId);
            summaryFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Payment failed or cart empty.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Orderable buildSelectedPaymentMethod() {
        String selected = (String) paymentCombo.getSelectedItem();
        if ("Credit Card".equals(selected)) {
            return new CreditCardPayment("4111 1111 1111 1111", currentUser.getUsername(), "12/30", "123");
        } else if ("Debit Card".equals(selected)) {
            return new DebitCardPayment("5000 0000 0000", currentUser.getUsername());
        } else {
            return new CashPayment("Cashier");
        }
    }

    public int placeOrderForTest(Orderable paymentMethod) {
        double total = cart.calculateTotal();
        if (cart.getItems().isEmpty()) return 0;
        if (!paymentService.process(paymentMethod, total)) return 0;

        int custId = resolveOrCreateCustomerId(currentUser);
        if (custId <= 0) return 0;

        Order order = new Order(0, custId, inferRestaurantIdFromCart(), total, "pending", null);
        for (CartItem ci : cart.getItems()) {
            order.addItem(new OrderItem(0, 0, ci.getItemId(), ci.getQuantity(), ci.getUnitPrice()));
        }
        boolean ok = orderDAO.createOrder(order);
        if (ok) {
            lastOrderId = order.getOrderId();
            cart.clearCart();
            return lastOrderId;
        }
        return 0;
    }

    private int resolveOrCreateCustomerId(User user) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            // Try resolve existing customer
            try (PreparedStatement ps = conn.prepareStatement("SELECT customer_id FROM customers WHERE user_id = ?")) {
                ps.setInt(1, user.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
            // Create minimal customer record
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO customers (user_id, address, phone) VALUES (?, 'Unknown', '000-0000')",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, user.getId());
                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) return keys.getInt(1); }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error resolving customer: " + ex.getMessage());
        }
        return 0;
    }

    private int inferRestaurantIdFromCart() {
        if (cart.getItems().isEmpty()) return 0;
        int firstItemId = cart.getItems().get(0).getItemId();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT restaurant_id FROM menu_items WHERE item_id = ?")) {
            ps.setInt(1, firstItemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.err.println("Error inferring restaurant id: " + ex.getMessage());
        }
        return 0;
    }

    public int getLastOrderId() { return lastOrderId; }
}
