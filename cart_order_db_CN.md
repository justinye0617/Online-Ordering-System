## 1 Shopping‑Cart Service：`shoppingcartdb`）

### 1.1 ER 关系示意

```
Cart 1 ──── n CartItem
```

- **Cart.id** ↔ **CartItem.cart_id**

### 1.2 表结构

#### 1.2.1 `cart`

| 列名       | 类型            | 约束                      | 说明                  |
| ---------- | --------------- | ------------------------- | --------------------- |
| id         | BIGINT UNSIGNED | PK, AUTO_INCREMENT        | 购物车主键            |
| user_id    | BIGINT UNSIGNED | NOT NULL, INDEX           | 拥有该购物车的用户 ID |
| created_at | DATETIME        | DEFAULT CURRENT_TIMESTAMP | 创建时间（可选）      |

```sql
CREATE TABLE cart (
  id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id    BIGINT UNSIGNED NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_cart_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 1.2.2 `cart_item`

| 列名       | 类型            | 约束                    | 说明          |
| ---------- | --------------- | ----------------------- | ------------- |
| id         | BIGINT UNSIGNED | PK, AUTO_INCREMENT      | 购物项主键    |
| cart_id    | BIGINT UNSIGNED | NOT NULL, FK → cart(id) | 关联购物车 ID |
| product_id | BIGINT UNSIGNED | NOT NULL                | 商品 ID       |
| quantity   | INT UNSIGNED    | NOT NULL                | 数量          |

```sql
CREATE TABLE cart_item (
  id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  cart_id    BIGINT UNSIGNED NOT NULL,
  product_id BIGINT UNSIGNED NOT NULL,
  quantity   INT UNSIGNED NOT NULL,
  CONSTRAINT fk_cartitem_cart
    FOREIGN KEY (cart_id) REFERENCES cart(id)
      ON DELETE CASCADE ON UPDATE CASCADE,
  KEY idx_cartitem_cart (cart_id),
  KEY idx_cartitem_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

------

## 2 Order Service（数据库：`orderdb`）

### 2.1 ER 关系示意

```
Order 1 ──── n OrderItem
```

- **orders.id** ↔ **order_item.order_id**

### 2.2 表结构

#### 2.2.1 `orders`

> **注意**：`order` 是 SQL 保留字，因此表名用 `orders`。

| 列名       | 类型            | 约束                      | 说明                       |
| ---------- | --------------- | ------------------------- | -------------------------- |
| id         | BIGINT UNSIGNED | PK, AUTO_INCREMENT        | 订单主键                   |
| user_id    | BIGINT UNSIGNED | NOT NULL, INDEX           | 下单用户 ID                |
| status     | VARCHAR(32)     | NOT NULL                  | 订单状态 (CREATED/PAID 等) |
| created_at | DATETIME        | DEFAULT CURRENT_TIMESTAMP | 创建时间                   |

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

| 列名       | 类型            | 约束                      | 说明               |
| ---------- | --------------- | ------------------------- | ------------------ |
| id         | BIGINT UNSIGNED | PK, AUTO_INCREMENT        | 订单项主键         |
| order_id   | BIGINT UNSIGNED | NOT NULL, FK → orders(id) | 关联订单 ID        |
| product_id | BIGINT UNSIGNED | NOT NULL                  | 商品 ID            |
| quantity   | INT UNSIGNED    | NOT NULL                  | 数量               |
| price      | DECIMAL(10,2)   | NOT NULL                  | 单价（下单时快照） |

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

------

## 3 索引与性能要点

| 目的                     | 方案                                                         |
| ------------------------ | ------------------------------------------------------------ |
| **快速按用户加载购物车** | `cart.user_id`、`orders.user_id` 单列索引                    |
| **订单状态过滤 & 查询**  | `orders.status` 索引                                         |
| **关联加载 / 级联删除**  | `ON DELETE CASCADE` 保证删除购物车或订单时同步删除其明细项   |
| **横向扩展**             | 未来可按 `user_id`（用户维度）或 `id` 范围进行分库分表；加上 Redis 缓存热点数据减轻 MySQL 压力 |

------

