# Entity-Relationship Diagram — Online Food Ordering System

This ER diagram shows the database schema and relationships between entities.

```mermaid
erDiagram
    USERS ||--o{ CUSTOMERS : "is a"
    CUSTOMERS ||--o{ ORDERS : "places"
    CUSTOMERS ||--o{ REVIEWS : "writes"
    RESTAURANTS ||--o{ MENU_ITEMS : "has"
    RESTAURANTS ||--o{ ORDERS : "receives"
    RESTAURANTS ||--o{ REVIEWS : "receives"
    ORDERS ||--o{ ORDER_ITEMS : "contains"
    ORDERS ||--|| PAYMENTS : "has"
    MENU_ITEMS ||--o{ ORDER_ITEMS : "included in"
    ORDERS ||--o| COUPONS : "applies"
    
    USERS {
        int user_id PK
        varchar username UK
        varchar email UK
        varchar password_hash
        enum user_type "customer, admin"
        timestamp created_at
    }
    
    CUSTOMERS {
        int customer_id PK
        int user_id FK
        varchar address
        varchar phone
    }
    
    RESTAURANTS {
        int restaurant_id PK
        varchar name
        varchar address
        varchar phone
        decimal rating
        timestamp created_at
    }
    
    MENU_ITEMS {
        int item_id PK
        int restaurant_id FK
        varchar name
        decimal price
        text description
        boolean available
    }
    
    ORDERS {
        int order_id PK
        int customer_id FK
        int restaurant_id FK
        decimal total_price
        enum status "pending, confirmed, preparing, shipped, delivered, cancelled"
        int coupon_id FK "nullable"
        timestamp created_at
    }
    
    ORDER_ITEMS {
        int order_item_id PK
        int order_id FK
        int item_id FK
        int quantity
        decimal unit_price
    }
    
    PAYMENTS {
        int payment_id PK
        int order_id FK
        enum payment_method "credit_card, debit_card, cash"
        decimal amount
        enum status "pending, completed, failed"
        timestamp payment_date
    }
    
    COUPONS {
        int coupon_id PK
        varchar code UK
        int discount_percent
        date valid_from
        date valid_until
        boolean active
    }
    
    REVIEWS {
        int review_id PK
        int restaurant_id FK
        int customer_id FK
        int rating "1-5"
        text comment
        timestamp created_at
    }
```

## Relationships Summary

| From | To | Relationship | Description |
|------|------|------------|-------------|
| USERS | CUSTOMERS | 1:1 | A user can be a customer (inheritance) |
| CUSTOMERS | ORDERS | 1:N | A customer can place many orders |
| CUSTOMERS | REVIEWS | 1:N | A customer can write many reviews |
| RESTAURANTS | MENU_ITEMS | 1:N | A restaurant has many menu items |
| RESTAURANTS | ORDERS | 1:N | A restaurant receives many orders |
| RESTAURANTS | REVIEWS | 1:N | A restaurant can have many reviews |
| ORDERS | ORDER_ITEMS | 1:N | An order contains many order items |
| ORDERS | PAYMENTS | 1:1 | Each order has one payment |
| ORDERS | COUPONS | N:1 (optional) | An order can apply one coupon |
| MENU_ITEMS | ORDER_ITEMS | 1:N | A menu item can be in many order items |

## Database Design Notes

- **users** table stores base authentication info
- **customers** table extends users with address and phone (OOP inheritance pattern)
- **password_hash** uses bcrypt or similar hashing (not plain text)
- **rating** in restaurants is calculated from reviews (average)
- **unit_price** in order_items stores the price at time of order (historical data)
- **coupon_id** in orders is nullable (optional feature)
- Timestamps track creation dates for audit trails
