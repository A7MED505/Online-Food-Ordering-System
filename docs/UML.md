# UML Class Diagram — Online Food Ordering System

The diagram below uses Mermaid and renders directly on GitHub.

```mermaid
classDiagram
    %% Core Users
    class User {
      +id: int
      +username: String
      +email: String
      -passwordHash: String
      +verifyPassword(raw: String): boolean
    }

    class Customer {
      +address: String
      +phone: String
    }

    User <|-- Customer

    %% Restaurant & Menu
    class Restaurant {
      +id: int
      +name: String
      +address: String
      +phone: String
      +rating: double
    }

    class MenuItem {
      +id: int
      +name: String
      +price: double
      +description: String
    }

    Restaurant "1" o-- "many" MenuItem

    %% Orders
    class Order {
      +id: int
      +status: OrderStatus
      +createdAt: LocalDateTime
      +total(): double
    }

    class OrderItem {
      +id: int
      +quantity: int
      +unitPrice: double
    }

    Order "1" o-- "many" OrderItem
    Customer "1" o-- "many" Order
    Order "1" --> "1" Restaurant

    %% Cart
    class Cart {
      +addItem(item: MenuItem, qty: int)
      +removeItem(itemId: int)
      +updateQuantity(itemId: int, qty: int)
      +total(): double
      +clear()
    }

    Customer "1" o-- "1" Cart

    %% Interfaces & Polymorphism
    interface Orderable {
      +placeOrder(order: Order): boolean
    }

    Orderable <|.. Order

    interface Payment {
      +process(amount: double): boolean
    }

    class CreditCardPayment
    class DebitCardPayment
    class CashPayment

    Payment <|.. CreditCardPayment
    Payment <|.. DebitCardPayment
    Payment <|.. CashPayment

    %% Enums
    class OrderStatus {
      <<enumeration>>
      PENDING
      CONFIRMED
      SHIPPED
      DELIVERED
    }
```

Notes:
- `Customer` inherits from `User` (Inheritance).
- Sensitive fields (e.g., `passwordHash`) are encapsulated.
- `Payment` demonstrates polymorphism via different implementations.
- `Orderable` interface is included per requirements.
