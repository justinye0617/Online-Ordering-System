## 📦 Overall Architecture

The project is a **microservice-based ordering system** comprising:

- **Menu Service (`menu-service`)** – Manages vendor menus.
- **Shopping Cart Service (`shoppingcart-service`)** – Manages user carts via Redis.
- **Order Service (`order-service`)** – Handles checkout, order creation, and payment.
- **Frontend** – `customer_local.html` and `vendor_local.html` provide interfaces for users and vendors.

------

## 🍽️ `menu-service`

### 🔧 Core Features

- Maintains per-vendor menus.
- Supports add, remove, and view operations.
- Data is persisted in MySQL (`menudb`).
- Uses Spring Boot + JPA + Swagger UI.

### 📄 Key Classes

- `Menu.java` – Entity for each vendor’s menu.
- `MenuItem.java` – Individual dish items.
- `MenuService.java` – Business logic.
- `MenuController.java` – REST API controller.

### 📑 API Documentation

| Method   | Endpoint           | Description                 | Params (Query)                        |
| -------- | ------------------ | --------------------------- | ------------------------------------- |
| `POST`   | `/api/menu/add`    | Add or update a dish        | `userId`, `name`, `price`, `imageUrl` |
| `DELETE` | `/api/menu/remove` | Remove a dish by product ID | `userId`, `productId`                 |
| `GET`    | `/api/menu/view`   | Get all dishes for a vendor | `userId`                              |
| `GET`    | `/api/menu/has`    | Check dish existence        | `userId`, `productId`                 |

------

## 🛒 `shoppingcart-service`

### 🔧 Core Features

- Per-user, per-vendor shopping carts.
- Data stored in Redis with 7-day TTL.
- Integrates with `menu-service` for product validation.
- Uses Spring Boot + Redis.

### 📄 Key Classes

- `Cart.java` – RedisHash for cart structure.
- `CartItem.java` – Individual item in cart.
- `CartService.java` – Core logic (add/remove/clear/view).
- `CartController.java` – REST API.

### 📑 API Documentation

| Method   | Endpoint           | Description           | Params (Query)                                               |
| -------- | ------------------ | --------------------- | ------------------------------------------------------------ |
| `POST`   | `/api/cart/add`    | Add item to cart      | `userId`, `vendorId`, `productId`, `name`, `imageUrl`, `quantity`, `price` |
| `DELETE` | `/api/cart/remove` | Remove item from cart | `userId`, `vendorId`, `productId`                            |
| `GET`    | `/api/cart/view`   | View cart contents    | `userId`, `vendorId`                                         |
| `DELETE` | `/api/cart/clear`  | Clear entire cart     | `userId`, `vendorId`                                         |

------

## 🧾 `order-service`

### 🔧 Core Features

- Creates orders from cart items.
- Integrates with both `menu-service` and `shoppingcart-service`.
- Stores data in MySQL (`orderdb`).
- Tracks order status (`CREATED` → `PAID`).

### 📄 Key Classes

- `Order.java` / `OrderItem.java` – JPA entities for orders.
- `OrderService.java` – Business logic: checkout and payment.
- `OrderController.java` – REST controller.
- `CartClient.java` & `MenuClient.java` – Feign clients for other services.

### 📑 API Documentation

| Method | Endpoint              | Description            | Params (Query / Path) |
| ------ | --------------------- | ---------------------- | --------------------- |
| `POST` | `/api/order/checkout` | Create order from cart | `userId`, `vendorId`  |
| `POST` | `/api/order/pay/{id}` | Pay for an order       | `{orderId}` (path)    |
| `GET`  | `/api/order/{id}`     | Get order details      | `{orderId}` (path)    |
| `GET`  | `/api/order/user`     | List user orders       | `userId`              |
| `GET`  | `/api/order/vendor`   | List vendor orders     | `vendorId`            |

------

## 🖥️ Frontend (`customer_local.html` and `vendor_local.html`)

### `customer_local.html`

- Shows the menu (GET from `menu-service`)
- Adds/removes items in cart (via `shoppingcart-service`)
- Checkout & pay (via `order-service`)

### `vendor_local.html`

- Add/remove menu items (via `menu-service`)
- View current menu

------

## 🔄 Data Flow (Simplified)

1. Customer views menu → `menu-service`
2. Adds items to cart → `shoppingcart-service`
3. Checks out → `order-service` validates menu + creates order
4. Cart is cleared
5. Customer pays → order status updates to `PAID`

