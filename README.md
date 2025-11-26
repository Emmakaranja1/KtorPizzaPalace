# 🍕 KtorPizzaPalace — Kotlin Backend API + Kotlin CLI Toolkit

A production-ready Pizza Restaurant Backend built using **Kotlin**, **Ktor**, **Exposed ORM**, **PostgreSQL**, **JWT Authentication**, and a full **Kotlin-based CLI** for interacting with the API.

This project is designed to demonstrate **clean backend architecture**, **API development**, **database design**, **CLI development**, and **test-driven development** using Kotlin.

---

## 🚀 Features

### 🔐 Authentication
- User & Admin registration
- Login with JWT token generation
- Role-based access (admin-only endpoints)

### 🍕 Pizza & Restaurant Module
- CRUD for Pizzas
- CRUD for Restaurants
- Link pizzas to restaurants with custom prices
- Validation (price ranges, required fields)

### 🗄 Database Layer
- PostgreSQL
- Exposed SQL ORM
- Flyway database migrations

### 💻 Kotlin CLI
A Kotlin command-line tool to:
- Register/login users
- View pizzas & restaurants
- Create pizzas/restaurants (admin only)
- Link pizzas to restaurants
- Test API endpoints quickly from terminal

### 🧪 Testing
- Ktor Server Tests
- Mock HTTP Client
- Testcontainers (PostgreSQL) for real DB testing

---

## 📂 Project Structure

```
KtorPizzaPalace/
│
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
│
├── backend/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── kotlin/
│       │   │   ├── app/
│       │   │   │   ├── Application.kt
│       │   │   │   ├── config/
│       │   │   │   ├── controllers/
│       │   │   │   ├── routes/
│       │   │   │   ├── models/
│       │   │   │   ├── services/
│       │   │   │   └── utils/
│       │   ├── resources/
│       │   │   ├── application.conf
│       │   │   └── db/migration/ (Flyway)
│       └── test/
│           └── kotlin/
│               ├── AuthTests.kt
│               ├── PizzaTests.kt
│               ├── RestaurantTests.kt
│               └── IntegrationTests.kt
│
├── cli/
│   ├── build.gradle.kts
│   └── src/
│       └── main/kotlin/
│           ├── cli/Main.kt
│           ├── cli/auth/LoginCommand.kt
│           ├── cli/pizza/PizzaCommand.kt
│           ├── cli/restaurant/RestaurantCommand.kt
│           └── utils/HttpClient.kt


---

## 🛠️ Installation & Setup

### 1️⃣ Clone the Repository
```sh
git clone https://github.com/YOUR_USERNAME/KtorPizzaPalace.git
cd KtorPizzaPalace
```

### 2️⃣ Configure PostgreSQL
Create a database:
```sql
CREATE DATABASE pizzadb;
```

### 3️⃣ Environment Variables
Create an `.env` file or export:

```
DB_URL=jdbc:postgresql://localhost:5432/pizzadb
DB_USER=postgres
DB_PASSWORD=yourpassword
JWT_SECRET=supersecretkey
```

---

## ▶️ Run the Backend Server

```sh
./gradlew run
```

Server runs at:

```
http://localhost:8080
```

---

## 💻 Run the Kotlin CLI

The CLI interacts with the running API.

```sh
cd cli/
./gradlew run
```

### Sample commands:

#### 🔐 Login
```
cli login --email=admin@pizza.com --password=secret
```

#### 🍕 List pizzas
```
cli pizza list
```

#### 🍽 Add new restaurant (admin)
```
cli restaurant create --name="Big Slice" --location="Nairobi"
```

#### 🔗 Link pizza to restaurant
```
cli pizza link --pizzaId=1 --restaurantId=3 --price=900
```

---

## 🔥 API Endpoints Overview

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register user |
| POST | `/auth/login` | Login & receive JWT |

### Pizzas
| Method | Endpoint |
|--------|----------|
| GET | `/pizzas` |
| POST | `/pizzas` |
| GET | `/pizzas/{id}` |

### Restaurants
| Method | Endpoint |
|--------|----------|
| GET | `/restaurants` |
| POST | `/restaurants` |
| GET | `/restaurants/{id}` |

### Restaurant-Pizza Relations
| Method | Endpoint |
|--------|----------|
| POST | `/restaurant-pizzas` |
| DELETE | `/restaurant-pizzas/{id}` |

---

## 🧪 Run All Tests

```sh
./gradlew test
```

---

## 🌱 Database Migrations (Flyway)

To apply migrations manually:

```sh
./gradlew flywayMigrate
```

---

## 🌟 Future Improvements
- Kotlin Multiplatform client
- Admin dashboard (web frontend)
- Email/SMS notifications
- Docker deployment
- Role-based admin UI management
- Search + filtering

---

## 📜 License
MIT — use it freely.

---

## 👩🏾‍💻 Author
**Emma Karanja**  
Software Developer & AI Engineer  
📧 **karanjaemmak@gmail.com**  
📞 **0748867064**
