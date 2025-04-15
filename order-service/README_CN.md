------

# Order Service

本项目是一个基于 Spring Boot 和 Spring Data JPA 的订单微服务示例，主要功能包括创建订单、模拟支付以及订单查询。该服务通常与购物车服务、用户服务、商品服务等其他微服务协同工作，提供完整的电商交易流程。

## 目录

- [项目概述](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#项目概述)
- [主要功能](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#主要功能)
- [技术栈](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#技术栈)
- [环境要求](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#环境要求)
- [快速开始](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#快速开始)
- [接口说明](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#接口说明)
    - [1. 创建订单](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#1-创建订单)
    - [2. 支付订单](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#2-支付订单)
    - [3. 查询订单详情](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#3-查询订单详情)
- [数据库结构](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#数据库结构)

------

## 项目概述

**Order Service** 通过一组 RESTful API，完成订单的**创建**、**支付**以及**查询**等操作。
 在分布式微服务架构中，该服务与其他服务（如购物车、库存、支付网关等）配合，实现电商平台的核心交易流程。

**核心流程：**

1. 接收来自前端或购物车服务的下单请求，生成订单并记录状态为 “CREATED”
2. 模拟支付操作，更新订单状态为 “PAID”
3. 可随时根据订单 ID 查询订单详情

------

## 主要功能

1. **创建订单**：通过请求体（JSON）传入用户 ID 与多个订单项（商品 ID、数量、价格等），生成并持久化订单记录
2. **模拟支付**：将已有订单的状态更新为 “PAID”，可在真实场景中对接第三方支付网关
3. **查询订单**：根据订单 ID 获取完整的订单与订单项信息

------

## 技术栈

- **Spring Boot** 2.x
- **Spring Data JPA**（基于 Hibernate 实现 ORM）
- **MySQL** 8.x
- **Lombok**（可选，用于简化实体与服务层的样板代码）
- **Maven**（构建与依赖管理）

------

## 环境要求

- **Java** 8 或更高版本
- **Maven** 3.x
- **MySQL** 5.7 或更高版本（已创建名为 `orderdb` 的数据库）

------

## 快速开始

1. **克隆或下载项目**

    ```bash
    git clone https://github.com/your-repo/order-service.git
    cd order-service
    ```

2. **修改配置文件**
     在 `src/main/resources/application.yml` 中，根据自己的数据库配置进行调整，示例如下：

    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/orderdb?useSSL=false&serverTimezone=UTC
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
      port: 8082
    ```

3. **构建并运行**

    ```bash
    # 构建项目
    mvn clean package
    
    # 运行项目
    java -jar target/order-service-0.0.1-SNAPSHOT.jar
    ```

4. **验证运行**

    - 默认情况下，服务会在 [http://localhost:8082](http://localhost:8082/) 启动
    - 可以通过 Postman 或任意 HTTP 工具来测试下面的接口

------

## 接口说明

### 1. 创建订单

- **Endpoint**: `POST /api/order/create`

- **Content-Type**: `application/json`

- **请求示例**:

    ```json
    {
      "userId": 1,
      "items": [
        {
          "productId": 101,
          "quantity": 2,
          "price": 10.0
        },
        {
          "productId": 102,
          "quantity": 1,
          "price": 20.0
        }
      ]
    }
    ```

- **响应示例**:

    ```json
    {
      "id": 10,
      "userId": 1,
      "status": "CREATED",
      "createdAt": "2025-04-14T15:03:23.456",
      "items": [
        {
          "id": 1001,
          "productId": 101,
          "quantity": 2,
          "price": 10.0
        },
        {
          "id": 1002,
          "productId": 102,
          "quantity": 1,
          "price": 20.0
        }
      ]
    }
    ```

    - `status` 字段初始为 **"CREATED"**
    - `items` 表示订单的所有商品信息

------

### 2. 支付订单

- **Endpoint**: `POST /api/order/pay/{orderId}`

- **Path Variable**:

    - `orderId`: 订单的主键 ID

- **请求示例**:

    ```
    POST /api/order/pay/10
    ```

- **响应示例**:

    ```json
    {
      "id": 10,
      "userId": 1,
      "status": "PAID",
      "createdAt": "2025-04-14T15:03:23.456",
      "items": [
        {
          "id": 1001,
          "productId": 101,
          "quantity": 2,
          "price": 10.0
        },
        {
          "id": 1002,
          "productId": 102,
          "quantity": 1,
          "price": 20.0
        }
      ]
    }
    ```

    - `status` 更改为 **"PAID"**
    - 当订单不存在时会返回 `404 Not Found`

------

### 3. 查询订单详情

- **Endpoint**: `GET /api/order/{orderId}`

- **Path Variable**:

    - `orderId`: 订单的主键 ID

- **请求示例**:

    ```
    GET /api/order/10
    ```

- **响应示例**:

    ```json
    {
      "id": 10,
      "userId": 1,
      "status": "PAID",
      "createdAt": "2025-04-14T15:03:23.456",
      "items": [
        {
          "id": 1001,
          "productId": 101,
          "quantity": 2,
          "price": 10.0
        },
        {
          "id": 1002,
          "productId": 102,
          "quantity": 1,
          "price": 20.0
        }
      ]
    }
    ```

    - 返回完整订单信息
    - 若该订单不存在则返回 `404 Not Found`

------

## 数据库结构

在 MySQL 中，服务可能会自动生成以下两张表（具体名称和字段可根据 JPA 注解或配置而略有不同）：

1. **orders 表**
    - `id` (BIGINT) - 主键，自增
    - `user_id` (BIGINT) - 关联用户ID
    - `status` (VARCHAR) - 订单状态，如 "CREATED"、"PAID"
    - `created_at` (DATETIME/TIMESTAMP) - 订单创建时间
2. **order_item 表**
    - `id` (BIGINT) - 主键，自增
    - `product_id` (BIGINT) - 产品ID
    - `quantity` (INT) - 购买数量
    - `price` (DOUBLE) - 商品单价
    - `order_id` (BIGINT) - 外键，关联到 orders 表的 id

------

