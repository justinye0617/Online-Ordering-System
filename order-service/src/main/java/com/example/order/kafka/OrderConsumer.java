package com.example.order.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.order.model.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void consume(String message) {
        try {
            Order order = objectMapper.readValue(message, Order.class);
            if (order.getQuantity() <= 0) {
                System.out.println("过滤掉无效订单: " + order);
                return;
            }
            System.out.println("Kafka 收到有效订单对象: " + order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
