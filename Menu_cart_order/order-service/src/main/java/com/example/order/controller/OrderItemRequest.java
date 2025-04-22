package com.example.order.controller;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long productId;
    private String name;
    private String imageUrl;
    private Integer quantity;
    private Double price;
}
