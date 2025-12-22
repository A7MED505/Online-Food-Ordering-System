# Source Code Structure

## Main Source Code (`src/main/java/com/foodordering/`)

### `models/`
Domain model classes (POJOs):
- `User.java` - Base user class
- `Customer.java` - Customer class (inherits User)
- `Restaurant.java` - Restaurant entity
- `MenuItem.java` - Menu item entity
- `Order.java` - Order entity
- `OrderItem.java` - Order item entity
- `Review.java` - Review entity
- `Coupon.java` - Coupon entity

### `dao/`
Data Access Objects for database operations:
- `UserDAO.java`
- `RestaurantDAO.java`
- `MenuItemDAO.java`
- `OrderDAO.java`
- `ReviewDAO.java`
- `CouponDAO.java`

### `services/`
Business logic and service classes:
- `Cart.java` - Shopping cart logic
- `Session.java` - User session management
- `OrderService.java` - Order processing

### `payments/`
Payment-related classes (Polymorphism):
- `CreditCardPayment.java`
- `DebitCardPayment.java`
- `CashPayment.java`

### `interfaces/`
Java interfaces:
- `Orderable.java` - Interface for orderable entities
- `Payment.java` - Payment interface

### `ui/`
User Interface classes (Swing/JavaFX):
- `LoginFrame.java`
- `RegisterFrame.java`
- `RestaurantFrame.java`
- `MenuFrame.java`
- `CartFrame.java`
- `CheckoutFrame.java`
- `OrderSummaryFrame.java`

### `utils/`
Utility classes:
- `DatabaseConnection.java` - Database connection manager
- `PasswordHasher.java` - Password encryption utility
- `ConfigLoader.java` - Configuration file loader

## Test Code (`src/test/java/com/foodordering/`)

Mirror structure of main code with `*Test.java` suffix:
- `models/` - Unit tests for model classes
- `dao/` - Unit tests for DAO classes
- `services/` - Unit tests for service classes
- `payments/` - Unit tests for payment classes

## Resources (`src/main/resources/`)

Configuration files:
- `database.properties` - Database connection settings (not in git)
- `database.properties.example` - Example configuration file
