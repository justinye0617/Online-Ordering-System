------

# Shopping Cart Service

该服务主要用于**管理用户的购物车**，包括：添加商品、移除商品以及查看购物车等功能。它是电商平台中将用户选购信息暂存并可后续下单的关键组成部分。

## 功能概述

1. **创建并管理购物车**
    - 当用户没有购物车时，会自动创建新的购物车。
    - 已存在购物车时可在其中添加或移除商品。
2. **添加商品到购物车**
    - 如果购物车中已存在同样的商品，自动累加购买数量。
3. **移除商品**
    - 从购物车中删除指定商品，如果商品不存在则返回 404。
4. **查看购物车内容**
    - 用户可查看购物车中的所有商品，以便确认或再次修改。

## 技术栈

- **Spring Boot**：提供内置容器和自动配置，简化 Web 项目的创建
- **Spring Data JPA**：基于 JPA 规范的数据访问层封装，简化数据库操作
- **MySQL**：存储购物车和购物项（Cart、CartItem）
- **Lombok**：减少样板代码（如 Getter / Setter / ToString 等）
- **Maven**：项目构建与依赖管理

## 项目结构

```
shoppingcart-service
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.shoppingcart
│   │   │       ├── controller
│   │   │       │   └── CartController.java
│   │   │       ├── model
│   │   │       │   ├── Cart.java
│   │   │       │   └── CartItem.java
│   │   │       ├── repository
│   │   │       │   ├── CartRepository.java
│   │   │       │   └── CartItemRepository.java
│   │   │       └── service
│   │   │           └── CartService.java
│   │   └── resources
│   │       └── application.yml
└── pom.xml
```

- **controller**: 对外暴露 RESTful API
- **model**: 数据实体类，对应数据库表
- **repository**: 数据访问层，与数据库进行 CRUD 交互
- **service**: 业务逻辑层，封装购物车的核心操作
- **application.yml**: 应用配置信息（数据库连接、端口号等）
- **pom.xml**: Maven 构建与依赖管理文件

## 数据库配置

在 `application.yml` 中指定了 MySQL 数据库信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shoppingcartdb?useSSL=false&serverTimezone=UTC
    username: root
    password: Dongyang0412!
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
server:
  port: 8081
```

- **url**：`jdbc:mysql://localhost:3306/shoppingcartdb?useSSL=false&serverTimezone=UTC`
    - 其中 `shoppingcartdb` 为数据库名，可自行修改
- **username** 与 **password**：数据库账号和密码
- **ddl-auto: update**：自动根据实体类更新表结构，方便开发环境使用
- **server.port=8081**：服务运行端口

> 如需在生产环境使用，请根据最佳实践修改数据库连接、端口、以及 DDL 策略等配置。

## 快速开始

1. **克隆或下载项目**

    ```bash
    git clone https://your-repo-url/shoppingcart-service.git
    ```

2. **导入项目**

    - 将项目导入到你的 IDE（如 IntelliJ、Eclipse）或在命令行环境使用 `mvn` 构建。

3. **配置数据库**

    - 确保已在 MySQL 中创建名为 `shoppingcartdb` 的数据库（若名称不同请在 `application.yml` 中修改对应配置）。
    - 在 `application.yml` 中设置正确的数据库账号和密码。

4. **启动服务**

    - 在 IDE 中运行 `ShoppingCartApplication`

    - 或在命令行中执行：

        ```bash
        mvn clean package
        java -jar target/shoppingcart-service-0.0.1-SNAPSHOT.jar
        ```

    - 默认在本地 **8081** 端口监听。

## API 说明

### 1. 添加商品到购物车

- **URL**: `POST /api/cart/add`

- **Query Parameters**:

    - `userId` (long): 用户 ID
    - `productId` (long): 商品 ID
    - `quantity` (int): 添加数量

- **示例**:

    ```
    POST http://localhost:8081/api/cart/add?userId=1&productId=101&quantity=2
    ```

- **成功响应** (`HTTP 200 OK`):

    ```json
    {
      "id": 3,
      "userId": 1,
      "items": [
        {
          "id": 5,
          "productId": 101,
          "quantity": 2
        }
      ]
    }
    ```

### 2. 从购物车中移除商品

- **URL**: `DELETE /api/cart/remove`

- **Query Parameters**:

    - `userId` (long): 用户 ID
    - `productId` (long): 商品 ID

- **示例**:

    ```
    DELETE http://localhost:8081/api/cart/remove?userId=1&productId=101
    ```

- **成功响应** (`HTTP 200 OK`):

    ```json
    {
      "id": 3,
      "userId": 1,
      "items": []
    }
    ```

- **若购物车或商品不存在**:

    - 返回 `404 Not Found` 或一个空响应，具体请查看应用日志。

### 3. 查看购物车内容

- **URL**: `GET /api/cart/view`

- **Query Parameter**:

    - `userId` (long): 用户 ID

- **示例**:

    ```
    GET http://localhost:8081/api/cart/view?userId=1
    ```

- **成功响应** (`HTTP 200 OK`):

    ```json
    {
      "id": 3,
      "userId": 1,
      "items": [
        {
          "id": 5,
          "productId": 101,
          "quantity": 2
        }
      ]
    }
    ```

- **若购物车不存在**:

    - 返回 `404 Not Found`。

