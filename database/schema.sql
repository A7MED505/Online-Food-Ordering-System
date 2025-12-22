-- ============================================
-- Online Food Ordering System - Database Schema
-- MySQL 8.0+
-- ============================================

-- Drop existing database if exists (for clean setup)
DROP DATABASE IF EXISTS food_ordering_system;

-- Create database
CREATE DATABASE food_ordering_system 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Use the database
USE food_ordering_system;

-- ============================================
-- Table: users
-- Base table for all user types (authentication)
-- ============================================
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    user_type ENUM('customer', 'admin') NOT NULL DEFAULT 'customer',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB;

-- ============================================
-- Table: customers
-- Extends users table (OOP Inheritance pattern)
-- ============================================
CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    address VARCHAR(255),
    phone VARCHAR(20),
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB;

-- ============================================
-- Table: restaurants
-- Stores restaurant information
-- ============================================
CREATE TABLE restaurants (
    restaurant_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20),
    rating DECIMAL(3,2) DEFAULT 0.00 CHECK (rating >= 0 AND rating <= 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_name (name),
    INDEX idx_rating (rating)
) ENGINE=InnoDB;

-- ============================================
-- Table: menu_items
-- Menu items belonging to restaurants
-- ============================================
CREATE TABLE menu_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    description TEXT,
    available BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(restaurant_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    
    INDEX idx_restaurant_id (restaurant_id),
    INDEX idx_name (name),
    INDEX idx_available (available)
) ENGINE=InnoDB;

-- ============================================
-- Table: coupons
-- Discount coupons
-- ============================================
CREATE TABLE coupons (
    coupon_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_percent INT NOT NULL CHECK (discount_percent > 0 AND discount_percent <= 100),
    valid_from DATE NOT NULL,
    valid_until DATE NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    
    INDEX idx_code (code),
    INDEX idx_active (active),
    INDEX idx_valid_dates (valid_from, valid_until)
) ENGINE=InnoDB;

-- ============================================
-- Table: orders
-- Customer orders
-- ============================================
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    restaurant_id INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL CHECK (total_price >= 0),
    status ENUM('pending', 'confirmed', 'preparing', 'shipped', 'delivered', 'cancelled') 
        NOT NULL DEFAULT 'pending',
    coupon_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(restaurant_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    FOREIGN KEY (coupon_id) REFERENCES coupons(coupon_id) 
        ON DELETE SET NULL 
        ON UPDATE CASCADE,
    
    INDEX idx_customer_id (customer_id),
    INDEX idx_restaurant_id (restaurant_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB;

-- ============================================
-- Table: order_items
-- Items within each order
-- ============================================
CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    
    FOREIGN KEY (order_id) REFERENCES orders(order_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    
    INDEX idx_order_id (order_id),
    INDEX idx_item_id (item_id)
) ENGINE=InnoDB;

-- ============================================
-- Table: payments
-- Payment records for orders
-- ============================================
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL UNIQUE,
    payment_method ENUM('credit_card', 'debit_card', 'cash') NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
    status ENUM('pending', 'completed', 'failed') NOT NULL DEFAULT 'pending',
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (order_id) REFERENCES orders(order_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    
    INDEX idx_order_id (order_id),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- ============================================
-- Table: reviews
-- Customer reviews for restaurants
-- ============================================
CREATE TABLE reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    customer_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(restaurant_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    
    INDEX idx_restaurant_id (restaurant_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_rating (rating),
    INDEX idx_created_at (created_at),
    
    -- Prevent duplicate reviews from same customer for same restaurant
    UNIQUE KEY unique_customer_restaurant (customer_id, restaurant_id)
) ENGINE=InnoDB;

-- ============================================
-- Triggers
-- ============================================

-- Trigger: Update restaurant rating after new review
DELIMITER $$

CREATE TRIGGER update_restaurant_rating_after_insert
AFTER INSERT ON reviews
FOR EACH ROW
BEGIN
    UPDATE restaurants
    SET rating = (
        SELECT AVG(rating)
        FROM reviews
        WHERE restaurant_id = NEW.restaurant_id
    )
    WHERE restaurant_id = NEW.restaurant_id;
END$$

CREATE TRIGGER update_restaurant_rating_after_update
AFTER UPDATE ON reviews
FOR EACH ROW
BEGIN
    UPDATE restaurants
    SET rating = (
        SELECT AVG(rating)
        FROM reviews
        WHERE restaurant_id = NEW.restaurant_id
    )
    WHERE restaurant_id = NEW.restaurant_id;
END$$

CREATE TRIGGER update_restaurant_rating_after_delete
AFTER DELETE ON reviews
FOR EACH ROW
BEGIN
    UPDATE restaurants
    SET rating = COALESCE((
        SELECT AVG(rating)
        FROM reviews
        WHERE restaurant_id = OLD.restaurant_id
    ), 0.00)
    WHERE restaurant_id = OLD.restaurant_id;
END$$

DELIMITER ;

-- ============================================
-- Views (Optional - for easier queries)
-- ============================================

-- View: Customer details with user info
CREATE VIEW v_customer_details AS
SELECT 
    c.customer_id,
    u.user_id,
    u.username,
    u.email,
    c.address,
    c.phone,
    u.created_at
FROM customers c
INNER JOIN users u ON c.user_id = u.user_id;

-- View: Order summary with customer and restaurant info
CREATE VIEW v_order_summary AS
SELECT 
    o.order_id,
    o.created_at AS order_date,
    u.username AS customer_name,
    c.phone AS customer_phone,
    r.name AS restaurant_name,
    o.total_price,
    o.status,
    p.payment_method,
    p.status AS payment_status
FROM orders o
INNER JOIN customers c ON o.customer_id = c.customer_id
INNER JOIN users u ON c.user_id = u.user_id
INNER JOIN restaurants r ON o.restaurant_id = r.restaurant_id
LEFT JOIN payments p ON o.order_id = p.order_id;

-- View: Menu items with restaurant info
CREATE VIEW v_menu_with_restaurant AS
SELECT 
    m.item_id,
    m.name AS item_name,
    m.price,
    m.description,
    m.available,
    r.restaurant_id,
    r.name AS restaurant_name,
    r.rating AS restaurant_rating
FROM menu_items m
INNER JOIN restaurants r ON m.restaurant_id = r.restaurant_id;

-- ============================================
-- Database Setup Complete
-- ============================================

-- Show tables
SHOW TABLES;
