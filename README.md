# Online Food Ordering System

A Java OOP application where customers can browse restaurant menus, add items to a cart, and place orders. The project uses MySQL (via MySQL Workbench) and JUnit 5 for testing. All project content and code are in English.

## Overview
- Browse restaurants and their menus
- Add items to a shopping cart
- Place orders and view order summaries
- Optional: multiple payment methods, ratings, discounts

## Tech Stack
- Java 17+
- JUnit 5
- MySQL Server + MySQL Workbench
- JDBC (Maven/Gradle build will be added)

## Folder Structure (planned)
```
src/
  main/java/com/foodordering/
    models/
    dao/
    services/
    payments/
    interfaces/
    ui/
    utils/
  test/java/com/foodordering/
    models/
    dao/
    services/
    payments/
    ui/
database/
  schema.sql
  sample_data.sql
docs/
  PROJECT_REQUIREMENTS.md
  UML.md
```

## Getting Started
1. Install Java 17+ and MySQL Server.
2. Clone the repository:
```bash
git clone https://github.com/A7MED505/Online-Food-Ordering-System.git
cd Online-Food-Ordering-System
```
3. Set up the database using MySQL Workbench (schema to be added under `database/`).
4. Initialize a build tool (Maven or Gradle) and configure JDBC connection settings.

## Testing
- JUnit 5 will be used for unit tests.
- Once Maven or Gradle is configured:
  - Maven: `mvn test`
  - Gradle: `gradle test`
