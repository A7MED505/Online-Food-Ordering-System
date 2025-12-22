# Database Documentation — Online Food Ordering System

## Overview
This document describes the database schema for the Online Food Ordering System. The database uses MySQL 8.0+ and follows a normalized relational design (3NF) to ensure data integrity and eliminate redundancy.

## Database Name
`food_ordering_system`

## Character Set
- Character Set: `utf8mb4`
- Collation: `utf8mb4_unicode_ci`
- Supports full Unicode including emojis and special characters

---

## Tables

### 1. `users`
**Purpose:** Base authentication table for all user types

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `user_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| `username` | VARCHAR(50) | NOT NULL, UNIQUE | Unique username |
| `email` | VARCHAR(100) | NOT NULL, UNIQUE | User email address |
| `password_hash` | VARCHAR(255) | NOT NULL | Hashed password (bcrypt) |
| `user_type` | ENUM | NOT NULL, DEFAULT 'customer' | User role (customer, admin) |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Account creation timestamp |

**Indexes:**
- `idx_username` on `username`
- `idx_email` on `email`

---

### 2. `customers`
**Purpose:** Customer-specific information (extends `users` table - OOP inheritance pattern)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `customer_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique customer identifier |
| `user_id` | INT | NOT NULL, UNIQUE, FK → users | Links to users table |
| `address` | VARCHAR(255) | NULL | Customer address (encapsulated) |
| `phone` | VARCHAR(20) | NULL | Phone number (encapsulated) |

**Foreign Keys:**
- `user_id` → `users(user_id)` ON DELETE CASCADE

**Indexes:**
- `idx_user_id` on `user_id`

---

### 3. `restaurants`
**Purpose:** Restaurant information

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `restaurant_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique restaurant identifier |
| `name` | VARCHAR(100) | NOT NULL | Restaurant name |
| `address` | VARCHAR(255) | NULL | Restaurant address |
| `phone` | VARCHAR(20) | NULL | Contact phone |
| `rating` | DECIMAL(3,2) | DEFAULT 0.00, CHECK (0-5) | Average rating |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation timestamp |

**Indexes:**
- `idx_name` on `name`
- `idx_rating` on `rating`

**Notes:**
- `rating` is auto-calculated from `reviews` table via triggers

---

### 4. `menu_items`
**Purpose:** Menu items for each restaurant

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `item_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique item identifier |
| `restaurant_id` | INT | NOT NULL, FK → restaurants | Parent restaurant |
| `name` | VARCHAR(100) | NOT NULL | Item name |
| `price` | DECIMAL(10,2) | NOT NULL, CHECK (≥0) | Item price |
| `description` | TEXT | NULL | Item description |
| `available` | BOOLEAN | DEFAULT TRUE | Availability status |

**Foreign Keys:**
- `restaurant_id` → `restaurants(restaurant_id)` ON DELETE CASCADE

**Indexes:**
- `idx_restaurant_id` on `restaurant_id`
- `idx_name` on `name`
- `idx_available` on `available`

---

### 5. `coupons`
**Purpose:** Discount coupons (optional feature)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `coupon_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique coupon identifier |
| `code` | VARCHAR(50) | NOT NULL, UNIQUE | Coupon code |
| `discount_percent` | INT | NOT NULL, CHECK (1-100) | Discount percentage |
| `valid_from` | DATE | NOT NULL | Start date |
| `valid_until` | DATE | NOT NULL | End date |
| `active` | BOOLEAN | DEFAULT TRUE | Active status |

**Indexes:**
- `idx_code` on `code`
- `idx_active` on `active`
- `idx_valid_dates` on `(valid_from, valid_until)`

---

### 6. `orders`
**Purpose:** Customer orders

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `order_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique order identifier |
| `customer_id` | INT | NOT NULL, FK → customers | Ordering customer |
| `restaurant_id` | INT | NOT NULL, FK → restaurants | Target restaurant |
| `total_price` | DECIMAL(10,2) | NOT NULL, CHECK (≥0) | Total order amount |
| `status` | ENUM | NOT NULL, DEFAULT 'pending' | Order status |
| `coupon_id` | INT | NULL, FK → coupons | Applied coupon (optional) |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Order timestamp |

**Status Values:**
- `pending` - Order placed, awaiting confirmation
- `confirmed` - Restaurant confirmed the order
- `preparing` - Order is being prepared
- `shipped` - Order is out for delivery
- `delivered` - Order completed
- `cancelled` - Order cancelled

**Foreign Keys:**
- `customer_id` → `customers(customer_id)` ON DELETE RESTRICT
- `restaurant_id` → `restaurants(restaurant_id)` ON DELETE RESTRICT
- `coupon_id` → `coupons(coupon_id)` ON DELETE SET NULL

**Indexes:**
- `idx_customer_id` on `customer_id`
- `idx_restaurant_id` on `restaurant_id`
- `idx_status` on `status`
- `idx_created_at` on `created_at`

---

### 7. `order_items`
**Purpose:** Individual items within an order

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `order_item_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique order item identifier |
| `order_id` | INT | NOT NULL, FK → orders | Parent order |
| `item_id` | INT | NOT NULL, FK → menu_items | Menu item reference |
| `quantity` | INT | NOT NULL, CHECK (>0) | Quantity ordered |
| `unit_price` | DECIMAL(10,2) | NOT NULL, CHECK (≥0) | Price at time of order |

**Foreign Keys:**
- `order_id` → `orders(order_id)` ON DELETE CASCADE
- `item_id` → `menu_items(item_id)` ON DELETE RESTRICT

**Indexes:**
- `idx_order_id` on `order_id`
- `idx_item_id` on `item_id`

**Notes:**
- `unit_price` stores historical price (in case menu price changes later)

---

### 8. `payments`
**Purpose:** Payment information for orders (polymorphism)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `payment_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique payment identifier |
| `order_id` | INT | NOT NULL, UNIQUE, FK → orders | Associated order |
| `payment_method` | ENUM | NOT NULL | Payment type |
| `amount` | DECIMAL(10,2) | NOT NULL, CHECK (≥0) | Payment amount |
| `status` | ENUM | NOT NULL, DEFAULT 'pending' | Payment status |
| `payment_date` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Payment timestamp |

**Payment Methods:**
- `credit_card` - Credit card payment
- `debit_card` - Debit card payment
- `cash` - Cash on delivery

**Payment Status:**
- `pending` - Payment not processed yet
- `completed` - Payment successful
- `failed` - Payment failed

**Foreign Keys:**
- `order_id` → `orders(order_id)` ON DELETE CASCADE

**Indexes:**
- `idx_order_id` on `order_id`
- `idx_status` on `status`

---

### 9. `reviews`
**Purpose:** Customer reviews for restaurants (optional feature)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `review_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique review identifier |
| `restaurant_id` | INT | NOT NULL, FK → restaurants | Reviewed restaurant |
| `customer_id` | INT | NOT NULL, FK → customers | Reviewing customer |
| `rating` | INT | NOT NULL, CHECK (1-5) | Star rating |
| `comment` | TEXT | NULL | Review text |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Review timestamp |

**Foreign Keys:**
- `restaurant_id` → `restaurants(restaurant_id)` ON DELETE CASCADE
- `customer_id` → `customers(customer_id)` ON DELETE CASCADE

**Indexes:**
- `idx_restaurant_id` on `restaurant_id`
- `idx_customer_id` on `customer_id`
- `idx_rating` on `rating`
- `idx_created_at` on `created_at`

**Unique Constraints:**
- `unique_customer_restaurant` on `(customer_id, restaurant_id)` - Prevents duplicate reviews

---

## Triggers

### `update_restaurant_rating_after_insert`
**Trigger Event:** AFTER INSERT on `reviews`
**Purpose:** Automatically updates restaurant average rating when a new review is added

### `update_restaurant_rating_after_update`
**Trigger Event:** AFTER UPDATE on `reviews`
**Purpose:** Updates restaurant rating when a review is modified

### `update_restaurant_rating_after_delete`
**Trigger Event:** AFTER DELETE on `reviews`
**Purpose:** Recalculates restaurant rating when a review is deleted

---

## Views

### `v_customer_details`
Combines `users` and `customers` tables for easy customer information retrieval
```sql
SELECT customer_id, user_id, username, email, address, phone, created_at
```

### `v_order_summary`
Comprehensive order view with customer, restaurant, and payment info
```sql
SELECT order_id, order_date, customer_name, customer_phone, restaurant_name,
       total_price, status, payment_method, payment_status
```

### `v_menu_with_restaurant`
Menu items with associated restaurant details
```sql
SELECT item_id, item_name, price, description, available,
       restaurant_id, restaurant_name, restaurant_rating
```

---

## OOP Mapping

### Inheritance
- **User → Customer:** Implemented via `users` table with `customers` extension table
- Database enforces 1:1 relationship with UNIQUE constraint on `user_id`

### Encapsulation
- Sensitive fields (`address`, `phone`) isolated in `customers` table
- `password_hash` stored in `users` (never plain text)

### Polymorphism
- **Payment types:** Represented via ENUM in `payments.payment_method`
- Java implementation will use interface `Payment` with concrete classes

### Interface
- **Orderable:** Implemented in Java for `Order` class (database stores result)

---

## Setup Instructions for MySQL Workbench

1. Open MySQL Workbench
2. Connect to your MySQL Server
3. Click **File → Open SQL Script**
4. Select `database/schema.sql`
5. Click **Execute** (lightning bolt icon)
6. Verify tables created: `SHOW TABLES;`

---

## Connection Details (for Java JDBC)

```properties
# database.properties (create in src/main/resources/)
db.url=jdbc:mysql://localhost:3306/food_ordering_system?useSSL=false&serverTimezone=UTC
db.username=root
db.password=YOUR_PASSWORD_HERE
db.driver=com.mysql.cj.jdbc.Driver
```

---

## Backup and Maintenance

### Regular Backup
```bash
mysqldump -u root -p food_ordering_system > backup_$(date +%Y%m%d).sql
```

### Restore from Backup
```bash
mysql -u root -p food_ordering_system < backup_20251222.sql
```

---

## Security Notes

1. **Never store plain passwords** - always use `password_hash`
2. **Use parameterized queries** in Java to prevent SQL injection
3. **Restrict database user privileges** - avoid using root in production
4. **Enable SSL** for remote connections
5. **Regular backups** before any schema changes

---

## Future Enhancements (Optional)

- Add `order_history` table for audit trail
- Add `admin` table extending `users`
- Add `notifications` table for order updates
- Add `delivery_tracking` table for real-time tracking
- Add full-text search indexes for menu items
