# Project Requirements — Online Food Ordering System

## Description
A Java OOP application where customers can browse restaurant menus, add items to a cart, and place orders. The project will use MySQL (via MySQL Workbench) for persistence and JUnit 5 for automated testing. All source code, documentation, and UI text are in English.

## Required Classes
- `MenuItem`
- `User`
- `Customer` (inherits `User`)
- `Order`
- `Restaurant`

## OOP Requirements
- **Inheritance:** `User → Customer`
- **Encapsulation:** Protect sensitive data (e.g., address, phone) via private fields and accessors
- **Polymorphism:** Different payment types
- **Interface:** `Orderable`

## Minimum Features
- View menu
- Add items to cart
- Place order
- Display order summary

## Optional Features
- Different payment methods (e.g., credit card, debit card, cash)
- Restaurant ratings
- Discounts or coupon codes

## Additional Functional Requirements
- Sign Up and Login screens with secure password handling (hashing)
- MySQL-backed persistence for users, restaurants, menu items, orders, and order items
- Basic session management for logged-in customers

## Non-Functional Requirements
- **Testing:** JUnit 5 unit tests and basic integration tests
- **Code Quality:** Clear naming, small cohesive classes, minimal duplication
- **Documentation:** README, UML class diagram, and concise API notes when needed


## Tech Stack
- Java 17+
- JUnit 5
- MySQL Server + MySQL Workbench
- JDBC (Maven/Gradle build to be added)

## Deliverables
- Source code (English-only)
- `README.md` with setup, usage, and testing instructions
- UML class diagram (Mermaid in `docs/UML.md`)
- SQL schema and seed data (to be added under `database/`)
