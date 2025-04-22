package com.example.order.service;
import com.example.order.client.MenuClient;
import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.repository.OrderRepository;
import com.example.order.client.CartClient;
import com.example.order.dto.*;

import java.time.LocalDateTime;
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

    @Autowired
    private MenuClient menuClient; // 用于校验商品是否存在

    @Transactional
    public Order checkout(Long userId, Long vendorId) {
        CartDTO cart = cartClient.getCart(userId, vendorId); // 修改客户端调用方法
        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("购物车为空，无法结账");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setVendorId(vendorId);
        order.setStatus("CREATED");
        order.setCheckoutTime(LocalDateTime.now());

        for (CartItemDTO c : cart.getItems()) {
            OrderItem oi = new OrderItem();
            Long productId = c.getProductId();
            if (!menuClient.existsInMenu(vendorId, productId)) {
                throw new IllegalArgumentException("商品不存在于菜单中");
            }
            oi.setProductId(c.getProductId());
            oi.setName(c.getName());
            oi.setImageUrl(c.getImageUrl());
            oi.setQuantity(c.getQuantity());
            oi.setPrice(c.getPrice());
            order.addItem(oi);
        }

        Order savedOrder = orderRepository.save(order);

        cartClient.clearCart(userId, vendorId); // 清空购物车

        return savedOrder;
    }

    @Transactional
    public Order payOrder(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus("PAID");
            return orderRepository.save(order);
        }
        return null;
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    public List<Order> getOrdersByVendorId(Long vendorId) {
        return orderRepository.findAllByVendorId(vendorId);
    }


    public Optional<Order> getOrder(Long orderId) {
        return orderRepository.findById(orderId);
    }
}
