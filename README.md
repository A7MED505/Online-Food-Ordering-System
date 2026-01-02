# Online Food Ordering System 🍕

A Java Swing-based desktop application for browsing restaurants, managing shopping carts, and placing food orders with comprehensive testing and validation.

## Features ✨

- **User Management** - Registration, login, profile management
- **Restaurant Browsing** - View restaurants with ratings and reviews
- **Menu Navigation** - Browse items with prices and descriptions
- **Shopping Cart** - Add/remove items, quantity management
- **Order Processing** - Place orders with status tracking
- **Payment System** - Multiple payment methods (Cash, Credit/Debit)
- **Reviews & Ratings** - Rate restaurants and leave feedback
- **Exception Handling** - Custom validation and error management
- **Session Management** - User authentication and authorization

## Tech Stack 🛠️

- **Language:** Java 17
- **GUI:** Swing
- **Database:** MySQL 8.0+
- **Build Tool:** Maven
- **Testing:** JUnit 5, Mockito
- **Architecture:** DAO Pattern, MVC

## Project Structure 📁

```
src/main/java/com/foodordering/
├── dao/           # Data Access Objects
├── exceptions/    # Custom exception classes
├── interfaces/    # Interface definitions
├── models/        # Domain models (User, Order, MenuItem, etc.)
├── payments/      # Payment processing
├── services/      # Business logic layer
├── ui/            # Swing UI frames
└── utils/         # Utilities (DB, Validation, Exception Handler)

src/test/java/com/foodordering/
├── dao/           # DAO tests
├── exceptions/    # Exception tests
├── integration/   # End-to-end integration tests
├── models/        # Model tests
├── payments/      # Payment tests
├── services/      # Service layer tests
├── ui/            # UI tests
└── utils/         # Utility tests

database/
└── schema.sql     # Database schema

docs/
├── DATABASE.md
├── ERD.md
├── PROJECT_REQUIREMENTS.md
└── UML.md
```

## Database Setup 🗄️

1. Install MySQL 8.0+
2. Create database:
```sql
mysql -u root -p < database/schema.sql
```
3. Configure connection in `src/main/resources/database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/food_ordering_system
db.username=your_username
db.password=your_password
```

## Installation & Running 🚀

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

### Steps
```bash
# Clone repository
git clone https://github.com/A7MED505/Online-Food-Ordering-System.git
cd Online-Food-Ordering-System

# Setup database
mysql -u root -p < database/schema.sql

# Configure database connection
cp src/main/resources/database.properties.example src/main/resources/database.properties
# Edit database.properties with your credentials

# Build project
mvn clean install

# Run application
mvn exec:java

# Run tests
mvn test
```

## Testing 🧪

**Test Coverage:** 148 tests across all layers

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserDAOTest

# Run with coverage
mvn clean test
```

**Test Categories:**
- Unit Tests: Models, DAOs, Services, Utilities
- Integration Tests: End-to-end user workflows
- UI Tests: Swing component validation

## Key Design Patterns 🎨

- **DAO Pattern** - Data access abstraction
- **Singleton** - DatabaseConnection, Session
- **Strategy Pattern** - Payment processing
- **MVC** - Separation of concerns
- **Exception Hierarchy** - Custom exception handling


## License 📄

This project is for educational purposes.
