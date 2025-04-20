package com.example.order.service;
import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.repository.OrderRepository;
import com.example.order.client.CartClient;
import com.example.order.dto.*;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartClient cartClient;

    @Transactional
    public Order createOrder(Long userId, List<OrderItem> orderItems) {
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("CREATED");
        for (OrderItem item : orderItems) {
            order.addItem(item);
        }
        return orderRepository.save(order);
    }

    @Transactional
    public Order payOrder(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isPresent()){
            Order order = optionalOrder.get();
            order.setStatus("PAID");
            return orderRepository.save(order);
        }
        return null;
    }

    @Transactional
    public Order checkout(Long userId) {
        CartDTO cart = cartClient.getCart(userId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("购物车为空，无法结账");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("CREATED");

        for (CartItemDTO c : cart.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setProductId(c.getProductId());
            oi.setName(c.getName());
            oi.setImageUrl(c.getImageUrl());
            oi.setQuantity(c.getQuantity());
            oi.setPrice(c.getPrice());
            order.addItem(oi);
        }
        Order savedOrder = orderRepository.save(order);

        // 清空购物车
        cartClient.clearCart(userId);

        return savedOrder;
    }

    public Optional<Order> getOrder(Long orderId) {
        return orderRepository.findById(orderId);
    }
}
