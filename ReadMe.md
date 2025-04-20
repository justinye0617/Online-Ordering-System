# 🍽️ Online Ordering System – Microservices

This project implements a simple online ordering system using **Spring Boot microservices**. It includes the following services:

- 📋 `menu-service`: Manages menu items for each restaurant.
- 🛒 `shoppingcart-service`: Manages the user's cart and items.
- 📦 `order-service`: Creates orders based on cart content and handles payment.

Each service uses its own **MySQL** database and exposes RESTful APIs. The services are connected via **Spring Cloud OpenFeign**.

---

## 📐 Architecture Overview

```
Frontend (HTML/JS)
   |
   v
+-----------+       +-----------------+        +----------------+
| menu.html | <---> |  Menu Service   |  <-->  | MySQL: menudb  |
+-----------+       +-----------------+        +----------------+
     ^
     |
+------------+      +----------------------+     +----------------------+
| customer.html --->| ShoppingCart Service |<--->| MySQL: shoppingcartdb|
+------------+      +----------------------+     +----------------------+
     |
     v
+------------+      +------------------+         +------------------+
| Checkout   | ---> |   Order Service  | <-----> | MySQL: orderdb   |
+------------+      +------------------+         +------------------+
```

---

## 📦 Project Modules

### 1. `menu-service`
- Port: `8089`
- DB: `menudb`
- Description: CRUD operations for restaurant menu items.

### 2. `shoppingcart-service`
- Port: `8081`
- DB: `shoppingcartdb`
- Description: Handles add/remove/view cart logic for customers.

### 3. `order-service`
- Port: `8082`
- DB: `orderdb`
- Description: Converts cart into order, and processes payments.

---

## 🚀 Quick Start

### Prerequisites
- Java 11+
- Maven 3+
- MySQL installed and running
- Ports 8081, 8082, 8089 must be available

### MySQL Setup

```sql
-- Create databases
CREATE DATABASE menudb;
CREATE DATABASE shoppingcartdb;
CREATE DATABASE orderdb;
```

### Run Services

Each module is an independent Spring Boot application.

```bash
cd menu-service && mvn spring-boot:run
cd shoppingcart-service && mvn spring-boot:run
cd order-service && mvn spring-boot:run
```

---

## 🧪 REST APIs

### 📋 Menu Service (`localhost:8089`)

| Method | Endpoint                     | Description        |
| ------ | ---------------------------- | ------------------ |
| POST   | `/api/menu/add`              | Add menu item      |
| DELETE | `/api/menu/remove`           | Remove menu item   |
| GET    | `/api/menu/view?userId={id}` | View menu for user |

**Example Add Menu Request:**
```
POST /api/menu/add?userId=1&productId=101&price=9.99
```

---

### 🛒 Cart Service (`localhost:8081`)

| Method | Endpoint                     | Description             |
| ------ | ---------------------------- | ----------------------- |
| POST   | `/api/cart/add`              | Add item to cart        |
| DELETE | `/api/cart/remove`           | Remove item from cart   |
| DELETE | `/api/cart/clear`            | Clear all items in cart |
| GET    | `/api/cart/view?userId={id}` | View user cart          |

**Example Add to Cart:**
```
POST /api/cart/add?userId=1&productId=101&quantity=2&price=9.99
```

---

### 📦 Order Service (`localhost:8082`)

| Method | Endpoint                          | Description            |
| ------ | --------------------------------- | ---------------------- |
| POST   | `/api/order/create`               | Create order manually  |
| POST   | `/api/order/checkout?userId={id}` | Create order from cart |
| POST   | `/api/order/pay/{orderId}`        | Pay for an order       |
| GET    | `/api/order/{orderId}`            | Get order details      |

**Example Checkout:**
```
POST /api/order/checkout?userId=1
```

---

## 🌐 Frontend Usage

### 1. `order-service/src/main/resources/static/customer.html`

- View menu
- Add items to cart
- View cart
- Checkout and create order

### 2. `order-service/src/main/resources/static/vendor.html`

- Add or remove menu items
- Manage restaurant's offerings

To use:
1. Open `customer.html` or `vendor.html` in a browser
2. Interact via buttons
3. Backend must be running and accessible at correct ports

---

## 🧰 Technologies Used

- Java 11
- Spring Boot 2.7.x
- Spring Data JPA
- Spring Cloud OpenFeign
- MySQL 8
- Lombok
- Springdoc OpenAPI (Swagger)
- HTML & Vanilla JavaScript (Frontend)

---

## 🔧 Swagger Docs

You can access Swagger UI at:

- **Menu**: [http://localhost:8089/swagger-ui.html](http://localhost:8089/swagger-ui.html)
- **Cart**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **Order**: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)

> Make sure all services are running and dependencies resolved.

---

