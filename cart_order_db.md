## 1 Shopping‑Cart Service: `shoppingcartdb`

### 1.1 ER Diagram

```
Cart 1 ──── n CartItem
```

- **Cart.id** ↔ **CartItem.cart_id**

### 1.2 Table Structures

#### 1.2.1 `cart`

| Column Name | Type            | Constraints               | Description                      |
| ----------- | --------------- | ------------------------- | -------------------------------- |
| id          | BIGINT UNSIGNED | PK, AUTO_INCREMENT        | Primary key of the cart          |
| user_id     | BIGINT UNSIGNED | NOT NULL, INDEX           | ID of the user who owns the cart |
| created_at  | DATETIME        | DEFAULT CURRENT_TIMESTAMP | Creation time (optional)         |

```sql
CREATE TABLE cart (
  id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id    BIGINT UNSIGNED NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_cart_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 1.2.2 `cart_item`

| Column Name | Type            | Constraints             | Description     |
| ----------- | --------------- | ----------------------- | --------------- |
| id          | BIGINT UNSIGNED | PK, AUTO_INCREMENT      | Primary key     |
| cart_id     | BIGINT UNSIGNED | NOT NULL, FK → cart(id) | Related cart ID |
| product_id  | BIGINT UNSIGNED | NOT NULL                | Product ID      |
| quantity    | INT UNSIGNED    | NOT NULL                | Quantity        |
| price       | DOUBLE UNSIGNED | NOT NULL                | Product price   |

```sql
CREATE TABLE cart_item (
  id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  cart_id    BIGINT UNSIGNED NOT NULL,
  product_id BIGINT UNSIGNED NOT NULL,
  quantity   INT UNSIGNED NOT NULL,
  price      DOUBLE UNSIGNED NOT NULL,
  CONSTRAINT fk_cartitem_cart
    FOREIGN KEY (cart_id) REFERENCES cart(id)
      ON DELETE CASCADE ON UPDATE CASCADE,
  KEY idx_cartitem_cart (cart_id),
  KEY idx_cartitem_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 2 Order Service: `orderdb`

### 2.1 ER Diagram

```
Order 1 ──── n OrderItem
```

- **orders.id** ↔ **order_item.order_id**

### 2.2 Table Structures

#### 2.2.1 `orders`

> **Note**: `order` is a reserved SQL keyword, so the table is named `orders`.

| Column Name | Type            | Constraints               | Description                         |
| ----------- | --------------- | ------------------------- | ----------------------------------- |
| id          | BIGINT UNSIGNED | PK, AUTO_INCREMENT        | Primary key of the order            |
| user_id     | BIGINT UNSIGNED | NOT NULL, INDEX           | ID of the user who placed the order |
| status      | VARCHAR(32)     | NOT NULL                  | Order status (CREATED/PAID/etc.)    |
| created_at  | DATETIME        | DEFAULT CURRENT_TIMESTAMP | Creation time                       |

```sql
CREATE TABLE orders (
  id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id    BIGINT UNSIGNED NOT NULL,
  status     VARCHAR(32) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_orders_user (user_id),
  KEY idx_orders_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 2.2.2 `order_item`

| Column Name | Type            | Constraints               | Description                         |
| ----------- | --------------- | ------------------------- | ----------------------------------- |
| id          | BIGINT UNSIGNED | PK, AUTO_INCREMENT        | Primary key                         |
| order_id    | BIGINT UNSIGNED | NOT NULL, FK → orders(id) | Related order ID                    |
| product_id  | BIGINT UNSIGNED | NOT NULL                  | Product ID                          |
| quantity    | INT UNSIGNED    | NOT NULL                  | Quantity                            |
| price       | DECIMAL(10,2)   | NOT NULL                  | Unit price (snapshot at order time) |

```sql
CREATE TABLE order_item (
  id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  order_id   BIGINT UNSIGNED NOT NULL,
  product_id BIGINT UNSIGNED NOT NULL,
  quantity   INT UNSIGNED NOT NULL,
  price      DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_orderitem_order
    FOREIGN KEY (order_id) REFERENCES orders(id)
      ON DELETE CASCADE ON UPDATE CASCADE,
  KEY idx_orderitem_order (order_id),
  KEY idx_orderitem_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 3 Indexing & Performance Considerations

| Goal                             | Solution                                                     |
| -------------------------------- | ------------------------------------------------------------ |
| **Fast cart loading by user**    | Single-column index on `cart.user_id`, `orders.user_id`      |
| **Order status filtering/query** | Index on `orders.status`                                     |
| **Cascading deletes/joins**      | `ON DELETE CASCADE` ensures related items are deleted with parent rows |
| **Horizontal scalability**       | Future sharding by `user_id` or `id` range; cache hot data in Redis to reduce MySQL load |