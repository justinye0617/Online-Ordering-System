# Online Ordering System

> **Table of Contents**
>
> 1. [Project Overview](https://chatgpt.com/c/6805b36b-6c4c-8000-918e-4a366807072f#project-overview)
> 2. [System Architecture](https://chatgpt.com/c/6805b36b-6c4c-8000-918e-4a366807072f#system-architecture)
> 3. [Microservices Description](https://chatgpt.com/c/6805b36b-6c4c-8000-918e-4a366807072f#microservices-description)
>      3.1 [menu‑service](https://chatgpt.com/c/6805b36b-6c4c-8000-918e-4a366807072f#31-menu-service-8089)
>      3.2 [shoppingcart‑service](https://chatgpt.com/c/6805b36b-6c4c-8000-918e-4a366807072f#32-shoppingcart-service-8081)
>      3.3 [order‑service](https://chatgpt.com/c/6805b36b-6c4c-8000-918e-4a366807072f#33-order-service-8082)
> 4. [Frontend UI](https://chatgpt.com/c/6805b36b-6c4c-8000-918e-4a366807072f#frontend-ui)
> 5. [Quick Start](https://chatgpt.com/c/6805b36b-6c4c-8000-918e-4a366807072f#quick-start)
> 6. [API Overview](https://chatgpt.com/c/6805b36b-6c4c-8000-918e-4a366807072f#api-overview)
> 7. [Data Model](https://chatgpt.com/c/6805b36b-6c4c-8000-918e-4a366807072f#data-model)
> 8. [Swagger Docs](https://chatgpt.com/c/6805b36b-6c4c-8000-918e-4a366807072f#swagger-docs)
> 9. [Deployment Suggestions](https://chatgpt.com/c/6805b36b-6c4c-8000-918e-4a366807072f#deployment-suggestions)
> 10. [TODO / Future Improvements](https://chatgpt.com/c/6805b36b-6c4c-8000-918e-4a366807072f#todo--future-improvements)

------

## Project Overview

This repository implements a **minimum viable distributed online ordering system**, including:

| Component                | Tech Stack                          | Persistence             | Default Port |
| ------------------------ | ----------------------------------- | ----------------------- | ------------ |
| **menu‑service**         | Spring Boot 2.7 + JPA               | MySQL (`menudb`)        | **8089**     |
| **shoppingcart‑service** | Spring Boot 2.7 + Spring Data Redis | Redis (`cart:{userId}`) | **8081**     |
| **order‑service**        | Spring Boot 2.7 + JPA + OpenFeign   | MySQL (`orderdb`)       | **8082**     |
| **Frontend (Static)**    | Plain HTML + Vanilla JS             | —                       | —            |

Key features:

- Vendors manage menu items
- Users browse the menu and add items to cart
- At checkout, **order‑service** fetches cart data from **shoppingcart‑service** and creates the order
- Swagger UI included in all services for easy testing

------

## System Architecture

- **menu‑service** persists menu items
- **shoppingcart‑service** uses Redis to store shopping carts with 7-day TTL
- **order‑service** uses OpenFeign to fetch the cart and perform "**one-click checkout & cart clearing**"

------

## Microservices Description

### 3.1 menu‑service (8089)

- **Responsibility**: Maintain a separate menu for each vendor (`userId`). Supports adding, deleting, and viewing items.
- **Database**: Two tables in `menudb`: `menu`, `menu_item`.
- **Key Features**:
    - `MenuService` auto-creates a menu if not present when adding items
    - Uses `@OneToMany(mappedBy="menu", cascade=CascadeType.ALL)` to save menu and items together
    - API documentation generated with `springdoc-openapi-ui`

### 3.2 shoppingcart‑service (8081)

- **Responsibility**: Session-based shopping cart using Redis; each item includes `price`, `quantity`, etc.
- **Data Model**: Uses `@RedisHash("cart")`; key format is `cart:{userId}`; TTL = 7 days
- **Key Features**:
    - `CartService#addItemToCart`: increments quantity if item exists, inserts new otherwise
    - `CartController`: RESTful APIs with CORS support
    - Use `DELETE /api/cart/clear` to clear cart after checkout

### 3.3 order‑service (8082)

- **Responsibility**: Order creation / payment / query and "create order from cart"
- **Key Classes**:
    - `CartClient` — OpenFeign client to reuse shopping cart API
    - `OrderService#checkout`: reads cart → creates order → saves order → calls `/api/cart/clear`
- **Database**: Tables `orders`, `order_item` in `orderdb`

------

## Frontend UI

| Page              | Role   | Features                                    |
| ----------------- | ------ | ------------------------------------------- |
| **customer.html** | User   | View menu, add to cart, view cart, checkout |
| **vendor.html**   | Vendor | Add/remove items, real-time menu refresh    |

> Both pages use **Vanilla JS Fetch** to call backend services. No frameworks, purely browser-native.

For deployment via Nginx / Apache, place the HTML under `/var/www/html/`, then access via `http://{host}/customer.html`.

### Local Debugging

- Change `host` constant to `"http://127.0.0.1"`
- Keep port mapping: 8089 (menu), 8081 (cart), 8082 (order)

------

## Quick Start

### 1 | Prepare Environment

- **JDK 17+** (Spring Boot 2.7 recommends 17)
- **Maven 3.9+**
- **MySQL 8** — create databases:

```sql
CREATE DATABASE menudb  DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE orderdb DEFAULT CHARACTER SET utf8mb4;
```

- **Redis 6+** — default: `redis://localhost:6379`

> **Security Note**: The DB password `Dongyang0412!` in `application.yml` is for demo only. Use env vars or secrets in real deployments.

### 2 | Build & Run

```bash
# Build all modules
mvn clean package -DskipTests

# Start each service
java -jar menu-service/target/menu-service-0.0.1-SNAPSHOT.jar \
     --server.port=8089

java -jar shoppingcart-service/target/shoppingcart-service-0.0.1-SNAPSHOT.jar \
     --server.port=8081

java -jar order-service/target/order-service-0.0.1-SNAPSHOT.jar \
     --server.port=8082 --cart.service.url=http://localhost:8081
```

You may also run individual modules via `mvn spring-boot:run`.

### 3 | Try the Flow

1. Open `http://localhost/customer.html` → browse menu → add items to cart
2. Click **Checkout** → order is created → cart is cleared
3. Browse API docs via `/swagger-ui.html`

------

## API Overview

> Only key APIs are listed. For full fields, check each service's Swagger docs.

### menu‑service

| Method   | Path               | Description     |
| -------- | ------------------ | --------------- |
| `POST`   | `/api/menu/add`    | Add/update item |
| `DELETE` | `/api/menu/remove` | Delete item     |
| `GET`    | `/api/menu/view`   | View menu       |

### shoppingcart‑service

| Method   | Path               | Description        |
| -------- | ------------------ | ------------------ |
| `POST`   | `/api/cart/add`    | Add item to cart   |
| `DELETE` | `/api/cart/remove` | Remove single item |
| `GET`    | `/api/cart/view`   | View cart          |
| `DELETE` | `/api/cart/clear`  | Clear cart         |

### order‑service

| Method | Path                               | Description                           |
| ------ | ---------------------------------- | ------------------------------------- |
| `POST` | `/api/order/create`                | Manually create an order              |
| `POST` | `/api/order/checkout?userId={uid}` | **Checkout** (create order from cart) |
| `POST` | `/api/order/pay/{orderId}`         | Mark order as paid                    |
| `GET`  | `/api/order/{orderId}`             | Get order details                     |

------

## Data Model

```
# MySQL (Simplified)
menu(id,user_id) ────< menu_item(id,menu_id,price,...)
orders(id,user_id,status,created_at) ────< order_item(id,order_id,quantity,price,...)

# Redis
cart:{userId} = {
  id: "{userId}",
  userId: 1001,
  items: [ {productId, name, quantity, price, imageUrl}, ... ],
  ttl: 604800
}
```

------

## Swagger Docs

Visit after service startup:

- `http://47.88.23.191:8089/swagger-ui.html`  (menu)
- `http://47.88.23.191:8081/swagger-ui.html`  (cart)
- `http://47.88.23.191:8082/swagger-ui.html`  (order)

