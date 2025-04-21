# 🛒 Order Service - Kafka + Spring Cloud Microservice

This module is part of a larger **Online Ordering System**, and implements the **Order Service** using Spring Boot, Apache Kafka, and Spring Cloud.  
It supports asynchronous order publishing, message filtering, transactional delivery, service registration via Eureka, and automated Kafka testing.

---

## 🚀 Tech Stack

- **Spring Boot 2.7.4**
- **Apache Kafka**
- **Spring Cloud (Eureka + OpenFeign)**
- **MySQL & Spring Data JPA**
- **EmbeddedKafka for testing**
- **Maven**

---

## 🧩 Features Overview

### ✅ Kafka Integration

| Feature | Description |
|--------|-------------|
| `KafkaTemplate` Producer | Converts Order objects into JSON and publishes to Kafka |
| `@KafkaListener` Consumer | Listens on `order-topic`, filters out invalid orders |
| JSON Serialization | Uses `Jackson` to serialize/deserialize message |
| Partition Strategy | Uses `orderId` as key to ensure message locality |
| Kafka Transactions | Configured with `transaction-id-prefix` for consistency |
| Send Callback | Logs Kafka delivery success/failure via async callbacks |

---

### ☁️ Spring Cloud Integration

| Feature | Description |
|--------|-------------|
| Eureka Client | `order-service` registers with the Eureka Server |
| `@EnableDiscoveryClient` | Automatically enables service discovery |
| OpenFeign | Future support for calling other microservices |
| Configuration | Centralized in `application.properties` or `.yml` |

---

### 🧪 Kafka Testing with Embedded Broker

A test suite using `@EmbeddedKafka` is implemented to validate message production/consumption at runtime.

```java
@EmbeddedKafka(partitions = 1, topics = "order-topic")
public class KafkaProducerTest {
    @Test
    public void testKafkaSendReceive() {
        // Given
        Order order = new Order("T001", "奶 tea", 1);

        // When
        orderProducer.sendOrder(order);

        // Then
        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "order-topic");
        assertThat(record.value()).contains("奶茶");
    }
}
```

---

## 📦 Module Structure

```
order-service/
├── controller/         # REST endpoints
├── kafka/              # Kafka producer/consumer
├── model/              # Order entity class
├── config/             # Kafka topic configuration
└── test/               # Kafka producer test with embedded broker
```

---

## 🔧 Build & Run

### Start Zookeeper + Kafka via Docker:

```bash
docker network create order-net

docker run -d --net order-net --name zookeeper -p 2181:2181 zookeeper

docker run -d --net order-net --name kafka -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  confluentinc/cp-kafka
```

### Build Project

```bash
mvn clean install
```

### Run Order Service

```bash
cd order-service
mvn spring-boot:run
```

---

## 🛠 REST API Endpoint

### POST `/orders/send`

```json
{
  "id": "10001",
  "product": "Bubble Tea",
  "quantity": 2
}
```

📥 Sends an order to Kafka for downstream processing.

