package com.onlineorder.order.controller;

import com.onlineorder.order.kafka.OrderProducer;
import com.onlineorder.order.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderProducer orderProducer;

    @PostMapping("/send")
    public ResponseEntity<String> sendOrder(@RequestBody Order order) {
        orderProducer.sendOrder(order);
        return ResponseEntity.ok("订单对象已发送到 Kafka: " + order);
    }
}
