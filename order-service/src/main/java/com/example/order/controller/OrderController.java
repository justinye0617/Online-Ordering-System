package com.example.order.controller;

import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.service.OrderService;
import com.example.order.controller.OrderRequest;
import com.example.order.controller.OrderItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        List<OrderItem> items = orderRequest.getItems().stream().map(req -> {
            OrderItem item = new OrderItem();
            item.setProductId(req.getProductId());
            item.setQuantity(req.getQuantity());
            item.setPrice(req.getPrice());
            return item;
        }).collect(Collectors.toList());

        Order order = orderService.createOrder(orderRequest.getUserId(), items);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/pay/{orderId}")
    public ResponseEntity<Order> payOrder(@PathVariable Long orderId) {
        Order order = orderService.payOrder(orderId);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        Optional<Order> order = orderService.getOrder(orderId);
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
