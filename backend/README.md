Pizza Ordering Backend API
A comprehensive Kotlin-based backend API for a pizza ordering system built with Ktor, Exposed ORM, and PostgreSQL.
Features

🔐 JWT-based authentication
👥 User management (customers, restaurant owners, admins)
🍕 Pizza catalog management
🏪 Restaurant management
📦 Order processing with status tracking
💳 Payment method support
💰 Smart pricing with fallback logic
🔄 RESTful API design

Tech Stack

Language: Kotlin 1.9+
Framework: Ktor 2.3+
Database: PostgreSQL
ORM: Exposed
Authentication: JWT (Auth0)
Password Hashing: BCrypt
Migrations: Flyway (optional)

Key Feature: Smart Pricing System 🎯
The API implements a fallback pricing mechanism that provides flexibility while maintaining data integrity:
How It Works

Restaurant-Specific Pricing (Priority):

If a pizza is explicitly listed in restaurant_pizzas table
Uses the custom price set by the restaurant
Respects availability flag


Base Price Fallback:

If a pizza is NOT listed under a restaurant
Automatically uses the base price from the pizzas table
Assumes the pizza is available



Benefits
✅ Flexibility: Restaurants can sell any pizza without pre-listing
✅ No Errors: Orders never fail due to missing restaurant-pizza entries
✅ Custom Pricing: Restaurants can still set custom prices when needed
✅ Availability Control: Listed pizzas can be marked unavailable
Example Scenarios
kotlin// Scenario 1: Pizza listed with custom price
// Restaurant A lists Margherita at $15.99
// Order uses $15.99 ✓

// Scenario 2: Pizza not listed
// Restaurant B doesn't list Margherita (base price: $12.99)
// Order automatically uses $12.99 ✓

// Scenario 3: Pizza listed but unavailable
// Restaurant C lists Margherita but marks it unavailable
// Order fails with clear error message ✗
Project Structure
backend/
├── src/main/kotlin/com/emmascode/
│   ├── config/          # Database configuration
│   ├── dto/            # Data Transfer Objects
│   ├── models/         # Database models (Exposed tables)
│   ├── repository/     # Data access layer
│   ├── routes/         # API route definitions
│   ├── services/       # Business logic layer
│   ├── utils/          # Helper utilities (JWT, hashing, responses)
│   ├── db/migration/   # SQL migration files
│   └── Main.kt         # Application entry point
└── .env                # Environment variables
Setup Instructions
1. Prerequisites

JDK 17 or higher
PostgreSQL 13+
Gradle 8.0+

2. Database Setup
   Create a PostgreSQL database:
   sqlCREATE DATABASE pizza_ordering_db;
3. Environment Configuration
   Create a .env file in the backend directory:
   bashcp .env.example .env
   Update the values in .env:
   envDB_URL=jdbc:postgresql://localhost:5432/pizza_ordering_db
   DB_USER=postgres
   DB_PASSWORD=your_password
   JWT_SECRET=your-secret-key-minimum-32-characters
4. Run Migrations (Optional)
   If using Flyway, migrations will run automatically. Otherwise, execute the SQL files manually:
   bashpsql -U postgres -d pizza_ordering_db -f src/main/kotlin/com/emmascode/db/migration/V1__Create_Users.sql
   psql -U postgres -d pizza_ordering_db -f src/main/kotlin/com/emmascode/db/migration/V2__Create_Pizzas.sql
# ... run remaining migrations
5. Build and Run
   bash# Build the project
   ./gradlew build

# Run the application
./gradlew run
The API will be available at http://localhost:8080
API Endpoints
Authentication

POST /auth/register - Register new user
POST /auth/login - Login user

Users

GET /users - Get all users
GET /users/{id} - Get user by ID
POST /users - Create user
PUT /users/{id} - Update user
DELETE /users/{id} - Delete user

Pizzas

GET /pizzas - Get all pizzas (query: ?category=classic)
GET /pizzas/{id} - Get pizza by ID
POST /pizzas - Create pizza
PUT /pizzas/{id} - Update pizza
DELETE /pizzas/{id} - Delete pizza

Restaurants

GET /restaurants - Get all restaurants (query: ?active=true, ?ownerId=1)
GET /restaurants/{id} - Get restaurant by ID
POST /restaurants - Create restaurant
PUT /restaurants/{id} - Update restaurant
DELETE /restaurants/{id} - Delete restaurant

Restaurant Pizzas

GET /restaurant-pizzas - Get all (query: ?restaurantId=1, ?pizzaId=1)
GET /restaurant-pizzas/{id} - Get by ID
POST /restaurant-pizzas - Add pizza to restaurant with custom price
PUT /restaurant-pizzas/{id} - Update pricing/availability
DELETE /restaurant-pizzas/{id} - Remove pizza from restaurant

Orders

GET /orders - Get all orders (query: ?userId=1, ?restaurantId=1, ?status=pending)
GET /orders/{id} - Get order by ID
POST /orders - Create order (uses smart pricing)
PUT /orders/{id} - Update order status
DELETE /orders/{id} - Delete order

🆕 Pricing Utilities

GET /restaurants/{restaurantId}/pizzas/{pizzaId}/price - Get effective price at restaurant
GET /restaurants/{restaurantId}/pizzas/{pizzaId}/availability - Check availability

Order Items

GET /order-items - Get all items (query: ?orderId=1)
GET /order-items/{id} - Get item by ID
PUT /order-items/{id} - Update item
DELETE /order-items/{id} - Delete item

Request/Response Examples
Register User
bashcurl -X POST http://localhost:8080/auth/register \
-H "Content-Type: application/json" \
-d '{
"username": "johndoe",
"email": "john@example.com",
"password": "securepassword123",
"role": "customer"
}'
Login
bashcurl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{
"email": "john@example.com",
"password": "securepassword123"
}'
Create Order (with automatic price calculation)
bashcurl -X POST http://localhost:8080/orders \
-H "Content-Type: application/json" \
-H "Authorization: Bearer YOUR_JWT_TOKEN" \
-d '{
"userId": 1,
"restaurantId": 1,
"deliveryAddress": "123 Main St, Nairobi",
"paymentMethod": "mobile_money",
"items": [
{
"pizzaId": 1,
"quantity": 2,
"specialInstructions": "Extra cheese"
},
{
"pizzaId": 3,
"quantity": 1
}
]
}'
Note: The API automatically calculates prices using restaurant-specific pricing if available, or falls back to base prices.
Check Pizza Price at Restaurant
bashcurl -X GET http://localhost:8080/restaurants/1/pizzas/5/price

# Response:
{
"success": true,
"data": {
"price": "15.99",
"isAvailable": true,
"isCustomPrice": true
}
}
Add Custom Price for Pizza at Restaurant
bashcurl -X POST http://localhost:8080/restaurant-pizzas \
-H "Content-Type: application/json" \
-d '{
"restaurantId": 1,
"pizzaId": 5,
"price": "18.99",
"isAvailable": true
}'
Database Schema
Users

User authentication and profile management
Roles: customer, restaurant_owner, admin

Pizzas

Global pizza catalog with base prices
Categories: classic, specialty, vegan

Restaurants

Restaurant information and ownership
Rating system

RestaurantPizzas (Optional)

Junction table linking pizzas to restaurants
Custom pricing per restaurant
Availability control
Not required for orders (fallback to base price)

Orders

Order tracking with status management
Automatic price calculation
Payment processing

OrderItems

Individual pizza items within orders
Prices calculated at order time
Special instructions support

Smart Pricing Implementation
Service Layer Logic
kotlin// In OrderService.kt
val pizza = pizzaRepository.findById(pizzaId)
val restaurantPizza = restaurantPizzaRepository.findByRestaurantAndPizza(restaurantId, pizzaId)

// Fallback logic
val priceToUse = restaurantPizza?.price ?: pizza.basePrice

// Availability check (only if listed)
if (restaurantPizza != null && !restaurantPizza.isAvailable) {
throw IllegalArgumentException("Pizza unavailable")
}
Key Methods

getPizzaPriceAtRestaurant(restaurantId, pizzaId) - Returns effective price
isPizzaAvailableAtRestaurant(restaurantId, pizzaId) - Checks availability

Security Features

Password hashing with BCrypt (12 rounds)
JWT-based authentication
Token expiry (24 hours default)
CORS configuration (development mode)

Development
Adding New Endpoints

Create DTO in dto/ package
Add model in models/ if needed
Create repository method in repository/
Implement business logic in services/
Define routes in routes/
Register routes in Main.kt

Testing
bash# Run tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport
Production Deployment

Update .env with production values
Set CORS to specific domains
Use HTTPS
Set up database backups
Configure logging
Use environment-specific JWT secrets
Consider caching for price lookups

Business Logic Best Practices
Price Management

Set base prices for all pizzas in the catalog
Optionally add restaurant-specific pricing
Let the API handle the fallback automatically

Restaurant Setup
Option A (Full Control):
- Add all pizzas to restaurant_pizzas with custom prices
- Control availability per pizza

Option B (Flexible):
- Don't add any pizzas to restaurant_pizzas
- All catalog pizzas available at base price

Option C (Hybrid - Recommended):
- Add popular pizzas with custom prices
- Other pizzas use base price automatically
  License
  MIT License