package com.foodordering.dao;

import com.foodordering.models.Order;
import com.foodordering.models.OrderItem;
import com.foodordering.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Order entity.
 */
public class OrderDAO {

    /**
     * Creates a new order with its items.
     */
    public boolean createOrder(Order order) {
        String orderSql = "INSERT INTO orders (customer_id, restaurant_id, total_price, status, coupon_id) VALUES (?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_items (order_id, item_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // Insert order
            try (PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setInt(1, order.getCustomerId());
                orderStmt.setInt(2, order.getRestaurantId());
                orderStmt.setDouble(3, order.getTotalPrice());
                orderStmt.setString(4, order.getStatus());
                if (order.getCouponId() != null) {
                    orderStmt.setInt(5, order.getCouponId());
                } else {
                    orderStmt.setNull(5, Types.INTEGER);
                }
                
                int affected = orderStmt.executeUpdate();
                if (affected > 0) {
                    try (ResultSet keys = orderStmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            order.setOrderId(keys.getInt(1));
                        }
                    }
                }
            }
            
            // Insert order items
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                try (PreparedStatement itemStmt = conn.prepareStatement(itemSql, Statement.RETURN_GENERATED_KEYS)) {
                    for (OrderItem item : order.getItems()) {
                        itemStmt.setInt(1, order.getOrderId());
                        itemStmt.setInt(2, item.getItemId());
                        itemStmt.setInt(3, item.getQuantity());
                        itemStmt.setDouble(4, item.getUnitPrice());
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback error: " + ex.getMessage());
                }
            }
            System.err.println("Error creating order: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error resetting autocommit: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Gets order by ID with its items.
     */
    public Order getOrderById(int orderId) {
        String orderSql = "SELECT * FROM orders WHERE order_id = ?";
        String itemsSql = "SELECT * FROM order_items WHERE order_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement orderStmt = conn.prepareStatement(orderSql);
             PreparedStatement itemsStmt = conn.prepareStatement(itemsSql)) {
            
            orderStmt.setInt(1, orderId);
            try (ResultSet rs = orderStmt.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order(
                            rs.getInt("order_id"),
                            rs.getInt("customer_id"),
                            rs.getInt("restaurant_id"),
                            rs.getDouble("total_price"),
                            rs.getString("status"),
                            (Integer) rs.getObject("coupon_id")
                    );
                    
                    // Load items
                    itemsStmt.setInt(1, orderId);
                    try (ResultSet itemsRs = itemsStmt.executeQuery()) {
                        while (itemsRs.next()) {
                            OrderItem item = new OrderItem(
                                    itemsRs.getInt("order_item_id"),
                                    itemsRs.getInt("order_id"),
                                    itemsRs.getInt("item_id"),
                                    itemsRs.getInt("quantity"),
                                    itemsRs.getDouble("unit_price")
                            );
                            order.addItem(item);
                        }
                    }
                    
                    return order;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order: " + e.getMessage());
        }
        return null;
    }

    /**
     * Gets all orders for a customer.
     */
    public List<Order> getOrdersByCustomerId(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                            rs.getInt("order_id"),
                            rs.getInt("customer_id"),
                            rs.getInt("restaurant_id"),
                            rs.getDouble("total_price"),
                            rs.getString("status"),
                            (Integer) rs.getObject("coupon_id")
                    );
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer orders: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Updates order status.
     */
    public boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
        }
        return false;
    }
}
