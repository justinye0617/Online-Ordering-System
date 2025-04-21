# 在线订餐系统（Online Ordering System）

> **目录**
>
> 1. [项目概览](https://chatgpt.com/c/6805b237-834c-8000-bbdd-453b794b69c3#项目概览)
> 2. [整体架构](https://chatgpt.com/c/6805b237-834c-8000-bbdd-453b794b69c3#整体架构)
> 3. [微服务说明](https://chatgpt.com/c/6805b237-834c-8000-bbdd-453b794b69c3#微服务说明)
>       3.1 [menu‑service](https://chatgpt.com/c/6805b237-834c-8000-bbdd-453b794b69c3#31-menu-service-8089)
>       3.2 [shoppingcart‑service](https://chatgpt.com/c/6805b237-834c-8000-bbdd-453b794b69c3#32-shoppingcart-service-8081)
>       3.3 [order‑service](https://chatgpt.com/c/6805b237-834c-8000-bbdd-453b794b69c3#33-order-service-8082)
> 4. [前端界面](https://chatgpt.com/c/6805b237-834c-8000-bbdd-453b794b69c3#前端界面)
> 5. [快速上手](https://chatgpt.com/c/6805b237-834c-8000-bbdd-453b794b69c3#快速上手)
> 6. [API 一览](https://chatgpt.com/c/6805b237-834c-8000-bbdd-453b794b69c3#api-一览)
> 7. [数据模型](https://chatgpt.com/c/6805b237-834c-8000-bbdd-453b794b69c3#数据模型)
> 8. [Swagger 在线文档](https://chatgpt.com/c/6805b237-834c-8000-bbdd-453b794b69c3#swagger-在线文档)
> 9. [部署与运维建议](https://chatgpt.com/c/6805b237-834c-8000-bbdd-453b794b69c3#部署与运维建议)
> 10. [TODO / 后续改进](https://chatgpt.com/c/6805b237-834c-8000-bbdd-453b794b69c3#todo--后续改进)

------

## 项目概览

本仓库实现了一个**分布式在线订餐系统**的最小可运行版本，包含：

| 组件                     | 技术栈                              | 持久化                  | 默认端口 |
| ------------------------ | ----------------------------------- | ----------------------- | -------- |
| **menu‑service**         | Spring Boot 2.7 + JPA               | MySQL (表 `menudb`)     | **8089** |
| **shoppingcart‑service** | Spring Boot 2.7 + Spring Data Redis | Redis (`cart:{userId}`) | **8081** |
| **order‑service**        | Spring Boot 2.7 + JPA + OpenFeign   | MySQL (表 `orderdb`)    | **8082** |
| **前端 (静态页面)**      | 原生 HTML + Vanilla JS              | —                       | —        |

核心功能：

- 商家管理菜品；
- 用户浏览菜单、将菜品加入购物车；
- 结账时由 **order‑service** 调用 **shoppingcart‑service** 搬运购物车数据并生成订单；
- 所有服务自带 Swagger UI，方便调试。

------

## 整体架构

- **menu‑service** 持久化菜品；
- **shoppingcart‑service** 使用 Redis 保存 7 天自动过期的购物车；
- **order‑service** 通过 OpenFeign 调用购物车服务实现“**一键结账&清空购物车**”。

------

## 微服务说明

### 3.1 menu‑service (8089)

- **职责**：为每个商家（`userId`）维护独立菜单。支持菜品的新增、删除、查看。
- **数据库**：`menudb` 中的两张表：`menu`、`menu_item`。
- **关键实现**
    - `MenuService` 在添加菜品时若菜单不存在则自动创建。
    - 通过 `@OneToMany(mappedBy="menu", cascade=CascadeType.ALL)` 保证菜单与菜单项级联保存。
    - `springdoc-openapi-ui` 自动生成接口文档。

### 3.2 shoppingcart‑service (8081)

- **职责**：基于 Redis 实现**会话级购物车**；每个条目包含 `price`、`quantity` 等字段。
- **数据模型**：`@RedisHash("cart")` 存储；键名形式 `cart:{userId}`；TTL = 7 天。
- **关键实现**
    - `CartService#addItemToCart` 若商品已存在则累加数量；否则插入新项。
    - `CartController` 提供 REST 风格接口并支持 CORS。
    - 可通过 `DELETE /api/cart/clear` 在结账后清空购物车。

### 3.3 order‑service (8082)

- **职责**：订单创建 / 支付 / 查询，以及“**从购物车生成订单**”。
- **重要类**
    - `CartClient` — OpenFeign 接口，复用购物车现有 API。
    - `OrderService#checkout`：读取购物车 → 构造订单 → 保存 → 调用 `/api/cart/clear`。
- **数据库**：`orders`、`order_item` 表存于 `orderdb`。

------

## 前端界面

| 页面              | 角色 | 功能点                                 |
| ----------------- | ---- | -------------------------------------- |
| **customer.html** | 顾客 | 展示菜单、加入购物车、查看购物车、结账 |
| **vendor.html**   | 商家 | 添加 / 删除菜品，实时刷新菜单          |

> 两个页面均以 **Vanilla JS Fetch** 调用后端；仅依赖浏览器原生能力，无任何框架。

若部署到 Nginx / Apache，可直接放到 `/var/www/html/` 等目录，通过 `http://{host}/customer.html` 访问。

### 本地调试

- 将 `host` 常量改为 `"http://127.0.0.1"`；
- 保持端口映射：8089 (menu)、8081 (cart)、8082 (order)。

------

## 快速上手

### 1 | 准备环境

- **JDK 17+**（Spring Boot 2.7 推荐 17）

- **Maven 3.9+**

- **MySQL 8** — 创建数据库：

    ```sql
    CREATE DATABASE menudb  DEFAULT CHARACTER SET utf8mb4;
    CREATE DATABASE orderdb DEFAULT CHARACTER SET utf8mb4;
    ```

- **Redis 6+** — 默认 `redis://localhost:6379`。

> **安全提示**：`application.yml` 中的数据库密码 `Dongyang0412!` 仅供演示，实际部署请改为环境变量或 Secrets。

### 2 | 编译 & 运行

```bash
# 编译所有模块
mvn clean package -DskipTests

# 逐个启动
java -jar menu-service/target/menu-service-0.0.1-SNAPSHOT.jar \
     --server.port=8089

java -jar shoppingcart-service/target/shoppingcart-service-0.0.1-SNAPSHOT.jar \
     --server.port=8081

java -jar order-service/target/order-service-0.0.1-SNAPSHOT.jar \
     --server.port=8082 --cart.service.url=http://localhost:8081
```

或使用 `mvn spring-boot:run` 快速启动单模块。

### 3 | 体验流程

1. 打开 `http://localhost/customer.html` → 浏览菜单 → 加入购物车。
2. 点击 **结账** → 生成订单 (orders) → 购物车被清空。
3. 使用 `/swagger-ui.html` 浏览接口文档。

------

## API 一览

> 以下仅列出核心接口，完整字段请参阅各服务 Swagger 文档。

### menu‑service

| 方法     | 路径               | 描述            |
| -------- | ------------------ | --------------- |
| `POST`   | `/api/menu/add`    | 添加 / 更新菜品 |
| `DELETE` | `/api/menu/remove` | 删除菜品        |
| `GET`    | `/api/menu/view`   | 获取菜单        |

### shoppingcart‑service

| 方法     | 路径               | 描述             |
| -------- | ------------------ | ---------------- |
| `POST`   | `/api/cart/add`    | 添加商品到购物车 |
| `DELETE` | `/api/cart/remove` | 移除单个商品     |
| `GET`    | `/api/cart/view`   | 查看购物车       |
| `DELETE` | `/api/cart/clear`  | 清空购物车       |

### order‑service

| 方法   | 路径                               | 描述                         |
| ------ | ---------------------------------- | ---------------------------- |
| `POST` | `/api/order/create`                | 手动创建订单                 |
| `POST` | `/api/order/checkout?userId={uid}` | **结账**（从购物车生成订单） |
| `POST` | `/api/order/pay/{orderId}`         | 标记订单为已支付             |
| `GET`  | `/api/order/{orderId}`             | 查询订单详情                 |

------

## 数据模型

```
# MySQL (简化)
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

## Swagger 在线文档

每个服务启动后访问：

- `http://47.88.23.191:8089/swagger-ui.html`  (menu)
- `http://47.88.23.191:8081/swagger-ui.html`  (cart)
- `http://47.88.23.191:8082/swagger-ui.html`  (order)



------

