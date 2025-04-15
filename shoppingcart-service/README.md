------

# Shopping Cart Service

This repository contains the **Shopping Cart Service**, a Spring Boot microservice that allows users to manage their shopping carts by adding and removing items, as well as viewing the current contents of their carts. It uses **MySQL** for data persistence and **Spring Data JPA** for data access.

## Table of Contents

- [Key Features](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#key-features)

- [Architecture Overview](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#architecture-overview)

- [Technology Stack](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#technology-stack)

- [Getting Started](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#getting-started)
    - [Prerequisites](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#prerequisites)
    - [Configuration](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#configuration)
    - [Build & Run](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#build--run)
    
- [API Endpoints](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#api-endpoints)
    - [Add Item to Cart](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#add-item-to-cart)
    - [Remove Item from Cart](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#remove-item-from-cart)
    - [View Cart](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#view-cart)
    
- [Database Schema](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#database-schema)

- [Example Usage](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#example-usage)

    

------

## Key Features

- **Create and manage carts**: Automatically creates a new cart if one does not exist for the given user.
- **Add items**: Allows the addition of items to a user’s cart, updating the quantity if the item already exists.
- **Remove items**: Enables removal of any existing item in the cart.
- **View cart**: Retrieves the current contents of the cart, including all item details.

------

## Architecture Overview

```
[Client/Frontend] --> [Shopping Cart Service] --> [MySQL Database]
```

1. **Client/Frontend**: Sends HTTP requests (POST, DELETE, GET) to manage the cart contents.
2. **Shopping Cart Service**: A Spring Boot application exposing RESTful endpoints, handling business logic, and persisting data.
3. **MySQL Database**: Stores `Cart` and `CartItem` records. Each user is associated with a single `Cart`, which in turn can contain multiple `CartItem`s.

------

## Technology Stack

- **Java 8+** / **Java 11+** (recommended)
- **Spring Boot** (Web, JPA)
- **MySQL** (database)
- **Maven** (build tool)
- **Lombok** (optional, for reducing boilerplate code)

------

## Getting Started

### Prerequisites

1. **Java** installed on your machine (Java 8 or higher).
2. **Maven** installed (for building the project).
3. **MySQL** running locally or accessible via network. Create a database named `shoppingcartdb` (or update the config accordingly).

### Configuration

By default, the service reads configuration from `src/main/resources/application.yml`. Key properties include:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shoppingcartdb?useSSL=false&serverTimezone=UTC
    username: root
    password: Dongyang0412!
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
server:
  port: 8081
```

- **spring.datasource.url**: Adjust host/port/database parameters if needed.
- **username** & **password**: Update with your own MySQL credentials.
- **server.port**: Defaults to **8081**. Change if the port is already in use.

### Build & Run

1. **Clone** this repository or copy the code into your local environment.

2. Open a terminal at the project’s root directory.

3. Execute the following command to build and run the service:

    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

4. Once started, the service will be available at:

    ```
    http://localhost:8081
    ```

------

## API Endpoints

Below are the main REST endpoints provided by the Shopping Cart Service. All endpoints are prefixed with `/api/cart`.

### Add Item to Cart

- **Method**: `POST`
- **URL**: `/api/cart/add`
- **Query Parameters**:
    - `userId` (Long, required) – User ID
    - `productId` (Long, required) – Product ID to add
    - `quantity` (Integer, required) – Number of items to add

**Example**

```
POST http://localhost:8081/api/cart/add?userId=1&productId=101&quantity=2
```

**Successful Response (JSON)**

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

### Remove Item from Cart

- **Method**: `DELETE`
- **URL**: `/api/cart/remove`
- **Query Parameters**:
    - `userId` (Long, required) – User ID
    - `productId` (Long, required) – Product ID to remove

**Example**

```
DELETE http://localhost:8081/api/cart/remove?userId=1&productId=101
```

**Successful Response (JSON)**

```json
{
  "id": 3,
  "userId": 1,
  "items": []
}
```

*(Items array is empty if successfully removed the only item. Otherwise, will contain remaining items.)*

### View Cart

- **Method**: `GET`
- **URL**: `/api/cart/view`
- **Query Parameters**:
    - `userId` (Long, required) – User ID

**Example**

```
GET http://localhost:8081/api/cart/view?userId=1
```

**Successful Response (JSON)**

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

------

## Database Schema

The Shopping Cart Service automatically creates or updates the schema (tables) based on your entities:

1. **Cart Table** (e.g., `cart`)
    - `id` (primary key, auto-increment)
    - `user_id` (refers to the associated user)
2. **CartItem Table** (e.g., `cart_item`)
    - `id` (primary key, auto-increment)
    - `product_id`
    - `quantity`
    - `cart_id` (foreign key referencing `cart.id`)

------

## Example Usage

1. **Start the service** (as described in [Build & Run](https://chatgpt.com/c/67fb65e9-f02c-8000-b9c2-1d5e30af60e2#build--run)).

2. **Add items** to the cart:

    ```bash
    curl -X POST "http://localhost:8081/api/cart/add?userId=1&productId=101&quantity=2"
    ```

3. **View** the cart:

    ```bash
    curl -X GET "http://localhost:8081/api/cart/view?userId=1"
    ```

4. **Remove** an item:

    ```bash
    curl -X DELETE "http://localhost:8081/api/cart/remove?userId=1&productId=101"
    ```

You can also use [Postman](https://www.postman.com/) or any other REST client to test these endpoints with the same URLs.

------

