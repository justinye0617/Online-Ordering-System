package com.example.order.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String name;
    private String imageUrl;
    private Integer quantity;
    private Double price;
}
