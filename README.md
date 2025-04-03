# 🛒 CommerceHub - E-commerce REST API

A simple e-commerce platform built with **Spring Boot (Java 21)**, **PostgreSQL**, and **Docker**, featuring RESTful endpoints for managing Products, Orders, and OrderItems.

## 🚀 Tech Stack

- **Java 21**
- **Spring Boot 3.4.4**
- **Spring Data JPA**
- **PostgreSQL**
- **OpenAPI 3.0** (via springdoc)
- **JUnit 5 & Mockito**
- **Docker & Docker Compose**
- **MapStruct & Lombok**

---

## 📦 Features

- Full **CRUD operations** for:
  - Products
  - Orders
  - Order Items
- **OpenAPI 3.0** documentation (Swagger UI)
- **Dockerized setup**
- Unit and integration tests using **JUnit** and **Mockito**
- Error handling with appropriate HTTP status codes

---

## 🛠️ Getting Started

### 1. Clone the Repository

```bash
cd commercehub
```

### 2. Run with Docker (Recommended)

Make sure Docker is installed.

```bash
docker-compose up --build
```

This spins up both the **PostgreSQL** database and the Spring Boot app.

### 3. API Documentation

Once the app is running, visit:

```
http://localhost:8080/swagger-ui.html
```

This provides interactive API documentation via OpenAPI 3.0.

---

## 🧪 Running Tests

You can run all unit and integration tests with:

```bash
./gradlew test
```

Test coverage includes:

- Service layer (business logic)
- REST controllers (using slice/integration tests)

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/example/commercehub/
│   │   ├── product/
│   │   ├── order/
│   │   └── orderitem/
│   └── resources/
└── test/
    └── java/com/example/commercehub/
```

---

## ✅ TODO / Improvements

- Pagination and filtering for large datasets
- Caching
- More tests
- Remove unused or unwanted objects
- Hateos

---

## 📄 License

MIT License
